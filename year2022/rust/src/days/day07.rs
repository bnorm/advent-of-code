use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let input: Vec<&str> = include_str!("res/input07.txt").lines().collect::<Vec<_>>();
    let file_system = FileSystem::new(&input);

    let mut total_size: usize = 0;
    for directory in &file_system.directories {
        let size = file_system.directory_size(directory);
        if size <= 100_000 {
            total_size += size;
        }
    }

    return format!("{:?}", total_size);
}

pub fn part2() -> String {
    let input: Vec<&str> = include_str!("res/input07.txt").lines().collect::<Vec<_>>();
    let file_system = FileSystem::new(&input);

    let free_space: usize = 70_000_000 - file_system.directory_size(&"/".to_string());
    let mut selected_size: usize = 70_000_000;
    for directory in &file_system.directories {
        let size = file_system.directory_size(directory);
        if free_space + size >= 30_000_000 && size < selected_size {
            selected_size = size;
        }
    }

    return format!("{:?}", selected_size);
}

#[derive(Debug, Clone, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"(?P<size>\d+) (?P<name>[a-z.]+)"#)]
struct FileInput {
    pub name: String,
    pub size: usize,
}

#[derive(Debug, Clone, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"dir (?P<name>[a-z.]+)"#)]
struct DirectoryInput {
    pub name: String,
}

#[derive(Debug, Clone, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"\$ cd (?P<path>[a-z./]+)"#)]
struct ChangeInput {
    pub path: String,
}

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
struct File {
    pub path: String,
    pub size: usize,
}

struct FileSystem {
    pub directories: Vec<String>,
    pub files: Vec<File>,
}

impl FileSystem {
    pub fn new(input: &Vec<&str>) -> Self {
        let mut directories = Vec::<String>::new();
        let mut files = Vec::<File>::new();

        let mut path = String::new();
        directories.push(String::from("/"));

        for line in input {
            if let Ok(file) = line.parse::<FileInput>() {
                files.push(File { path: format!("{}/{}", path, file.name), size: file.size });
            } else if let Ok(directory) = line.parse::<DirectoryInput>() {
                directories.push(format!("{}/{}/", path, directory.name)); // trailing `/` is very important!!!
            } else if let Ok(change) = line.parse::<ChangeInput>() {
                match change.path.as_str() {
                    "/" => continue,
                    ".." => path.replace_range(path.rfind('/').unwrap()..path.len(), ""),
                    _ => {
                        path.push_str("/");
                        path.push_str(&change.path);
                    }
                }
            }
        }

        return Self { directories, files };
    }

    pub fn directory_size(&self, dir: &String) -> usize {
        let mut size = 0;

        for file in &self.files {
            if file.path.starts_with(dir) {
                size += file.size;
            }
        }

        return size;
    }
}
