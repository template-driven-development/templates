package main

import (
	"encoding/json"
	"os"
	"os/exec"
	"path/filepath"
	"testing"
)

const moduleRoot = ".."

var templateDirs = []string{
	filepath.Join(moduleRoot, "scala"),
}

var testDir = filepath.Join(moduleRoot, "test")

type Arguments struct {
	Input    string      `json:"input"`
	Output   string      `json:"output"`
	Data     interface{} `json:"data"`
	Snapshot string      `json:"snapshot"`
}

func collectTemplateRelativePaths() ([]string, error) {
	var templates []string

	for _, dir := range templateDirs {
		err := filepath.WalkDir(dir, func(path string, d os.DirEntry, err error) error {
			if err != nil {
				return err
			}
			if d.IsDir() {
				return nil
			}
			rel, err := filepath.Rel(moduleRoot, path)
			if err != nil {
				return err
			}
			templates = append(templates, rel)
			return nil
		})
		if err != nil {
			return nil, err
		}
	}

	return templates, nil
}

func templateAbsolutePath(templateRelativePath string) (string, error) {
	rel := filepath.Join(moduleRoot, templateRelativePath)
	return filepath.Abs(rel)
}

func snapshotAbsolutePath(templateRelativePath string) (string, error) {
	rel := filepath.Join(testDir, templateRelativePath, "snapshot")
	return filepath.Abs(rel)
}

func dataFromFile(templateRelativePath string) (interface{}, error) {
	rel := filepath.Join(testDir, templateRelativePath, "data.json")
	abs, err := filepath.Abs(rel)
	if err != nil {
		return nil, err
	}

	bytes, err := os.ReadFile(abs)
	if err != nil {
		return nil, err
	}

	var data interface{}
	err = json.Unmarshal(bytes, &data)
	if err != nil {
		return nil, err
	}

	return data, nil
}

func buildTddArguments(templateRelativePath string) (*Arguments, error) {
	input, err := templateAbsolutePath(templateRelativePath)
	if err != nil {
		return nil, err
	}

	data, err := dataFromFile(templateRelativePath)
	if err != nil {
		return nil, err
	}

	output, err := os.CreateTemp("", filepath.Base(templateRelativePath))
	if err != nil {
		return nil, err
	}

	snapshot, err := snapshotAbsolutePath(templateRelativePath)
	if err != nil {
		return nil, err
	}

	return &Arguments{
		Input:    input,
		Output:   output.Name(),
		Data:     data,
		Snapshot: snapshot,
	}, nil
}

func writeTddArguments(args Arguments) (*os.File, error) {
	file, err := os.CreateTemp("", "args.json")
	if err != nil {
		return nil, err
	}

	jsonData, err := json.Marshal([]Arguments{args})
	if err != nil {
		return nil, err
	}

	if err := os.WriteFile(file.Name(), jsonData, os.ModePerm); err != nil {
		return nil, err
	}

	return file, nil
}

func compareOutputAndSnapshot(args Arguments) error {
	output, err := os.ReadFile(args.Output)
	if err != nil {
		return err
	}

	snapshot, err := os.ReadFile(args.Snapshot)
	if err != nil {
		return err
	}

	if string(output) != string(snapshot) {
		return os.ErrInvalid
	}

	return nil
}

func testSnapshot(t *testing.T, templateRelativePath string) {
	args, err := buildTddArguments(templateRelativePath)
	if err != nil {
		t.Fatalf("Failed to create arguments for template %s: %v", templateRelativePath, err)
	}

	argsFile, err := writeTddArguments(*args)
	if err != nil {
		t.Fatalf("Failed to write arguments for template %s: %v", templateRelativePath, err)
	}

	out, err := exec.Command("tdd", argsFile.Name()).CombinedOutput()
	if err != nil {
		t.Fatalf("Failed to execute TDD for template %s with %s: %v", templateRelativePath, argsFile.Name(), string(out))
	}

	if err := compareOutputAndSnapshot(*args); err != nil {
		t.Errorf("Template %s does not match snapshot: %s, %s", templateRelativePath, args.Output, args.Snapshot)
	}

	t.Logf("Template %s matches snapshot", templateRelativePath)
}

func TestSnapshots(t *testing.T) {
	templates, err := collectTemplateRelativePaths()
	if err != nil {
		t.Fatalf("Failed to retrieve templates: %v", err)
	}

	for _, template := range templates {
		testSnapshot(t, template)
	}
}
