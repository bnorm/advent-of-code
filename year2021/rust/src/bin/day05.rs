use std::fs;
use std::collections::HashMap;

use itertools::Itertools;

use year2021::geo::{intersection, Line, Point};

fn main() {
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
}

fn part1() -> Option<usize> {
    let filename = "res/input05.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let lines = contents.lines()
        .filter_map(|line| line.parse::<Line>().ok())
        .filter(|line| line.p1.x == line.p2.x || line.p1.y == line.p2.y)
        .collect_vec();

    let mut grid: HashMap<Point, i32> = HashMap::new();

    for i1 in 0..lines.len() {
        for i2 in i1 + 1..lines.len() {
            let l1 = &lines[i1];
            let l2 = &lines[i2];

            for i in intersection(l1, l2) {
                match grid.get_mut(&i) {
                    Some(v) => { *v += 1; }
                    None => { grid.insert(i, 1); }
                }
            }
        }
    }

    return Some(grid.len());
}

fn part2() -> Option<usize> {
    let filename = "res/input05.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let lines = contents.lines()
        .filter_map(|line| line.parse::<Line>().ok())
        .collect_vec();

    let mut grid: HashMap<Point, i32> = HashMap::new();

    for i1 in 0..lines.len() {
        for i2 in i1 + 1..lines.len() {
            let l1 = &lines[i1];
            let l2 = &lines[i2];

            for i in intersection(l1, l2) {
                match grid.get_mut(&i) {
                    Some(v) => { *v += 1; }
                    None => { grid.insert(i, 1); }
                }
            }
        }
    }

    return Some(grid.len());
}
