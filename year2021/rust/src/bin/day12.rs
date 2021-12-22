use std::collections::{HashMap, HashSet};
use std::collections::hash_map::Entry;
use std::fs;
use std::time::Instant;

use itertools::Itertools;
use recap::Recap;
use serde::Deserialize;

#[derive(Debug, Deserialize, Recap)]
#[recap(regex = r#"(?P<start>[a-zA-Z]+)-(?P<end>[a-zA-Z]+)"#)]
struct Input {
    start: String,
    end: String,
}

#[derive(Debug)]
struct Node {
    name: String,
    paths: Vec<String>,
}

#[derive(Debug)]
struct Graph {
    nodes: HashMap<String, Node>,
}

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u64> {
    let instant = Instant::now();
    let graph = read_input()?;

    let start = &graph.nodes["start"];
    let end = &graph.nodes["end"];
    let result = count_paths(&graph, start, end, &HashSet::new(), true);

    println!("[part1] time={:?}", instant.elapsed());
    return Some(result);
}

fn part2() -> Option<u64> {
    let instant = Instant::now();
    let graph = read_input()?;

    let start = &graph.nodes["start"];
    let end = &graph.nodes["end"];
    let result = count_paths(&graph, start, end, &HashSet::new(), false);

    println!("[part2] time={:?}", instant.elapsed());
    return Some(result);
}

fn read_input() -> Option<Graph> {
    let filename = "res/input12.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let paths = contents.lines()
        .filter_map(|line| line.parse::<Input>().ok())
        .collect_vec();

    let mut nodes: HashMap<String, Node> = HashMap::new();
    for path in paths {
        let start = match nodes.entry(path.start.to_string()) {
            Entry::Occupied(o) => o.into_mut(),
            Entry::Vacant(v) => v.insert(Node {
                name: path.start.to_string(),
                paths: vec!(),
            })
        };
        start.paths.push(path.end.to_string());

        let end = match nodes.entry(path.end.to_string()) {
            Entry::Occupied(o) => o.into_mut(),
            Entry::Vacant(v) => v.insert(Node {
                name: path.end.to_string(),
                paths: vec!(),
            })
        };
        end.paths.push(path.start.to_string());
    }

    return Some(Graph { nodes });
}

fn count_paths(
    graph: &Graph,
    current: &Node,
    end: &Node,
    memory: &HashSet<&String>,
    doubled: bool,
) -> u64 {
    if current.name == end.name { return 1; }

    let mut memory = memory.clone();
    if current.name.to_uppercase() != current.name {
        memory.insert(&current.name);
    }

    let mut count = 0;
    for path in &current.paths {
        if path == "start" { continue; }

        if !memory.contains(path) {
            count += count_paths(graph, &graph.nodes[path], end, &memory, doubled);
        } else if !doubled {
            count += count_paths(graph, &graph.nodes[path], end, &memory, true);
        }
    }

    return count;
}
