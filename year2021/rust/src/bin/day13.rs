use std::collections::hash_map::Entry;
use std::collections::{HashMap, HashSet};
use std::fs;
use std::time::Instant;

use itertools::Itertools;
use recap::Recap;
use serde::Deserialize;

#[derive(Debug, PartialEq, Eq, Hash, Deserialize, Recap)]
#[recap(regex = r#"(?P<x>\d+),(?P<y>\d+)"#)]
struct Point {
    x: u64,
    y: u64,
}

#[derive(Debug, Deserialize, Recap)]
#[recap(regex = r#"fold along (?P<axis>[xy])=(?P<value>\d+)"#)]
struct Fold {
    axis: String,
    value: u64,
}

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u64> {
    let instant = Instant::now();
    let (points, folds) = read_input()?;

    let mut points = points;
    let first = &folds[0];
    if first.axis == "x" {
        points = fold_x(first.value, &points);
    } else {
        points = fold_y(first.value, &points);
    }

    println!("[part1] time={:?}", instant.elapsed());
    return Some(points.len() as u64);
}

fn part2() -> Option<u64> {
    let instant = Instant::now();
    let (points, folds) = read_input()?;

    let mut points = points;
    for fold in folds {
        if fold.axis == "x" {
            points = fold_x(fold.value, &points);
        } else {
            points = fold_y(fold.value, &points);
        }
    }

    let max_x = points.iter().map(|p| p.x).max()?;
    let max_y = points.iter().map(|p| p.y).max()?;

    for y in 0..max_y + 1 {
        for x in 0..max_x + 1 {
            if points.contains(&Point { x, y }) {
                print!("#");
            } else {
                print!(".");
            }
        }
        println!()
    }

    println!("[part2] time={:?}", instant.elapsed());
    return None;
}

fn read_input() -> Option<(Vec<Point>, Vec<Fold>)> {
    let filename = "res/input13.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let points = contents
        .lines()
        .filter_map(|line| line.parse::<Point>().ok())
        .collect_vec();

    let folds = contents
        .lines()
        .filter_map(|line| line.parse::<Fold>().ok())
        .collect_vec();

    return Some((points, folds));
}

fn fold_x(x: u64, points: &Vec<Point>) -> Vec<Point> {
    let mut folded: HashSet<Point> = HashSet::new();

    for p in points {
        if p.x < x {
            folded.insert(Point { x: p.x, y: p.y });
        } else {
            folded.insert(Point {
                x: 2 * x - p.x,
                y: p.y,
            });
        }
    }

    return folded.into_iter().collect();
}

fn fold_y(y: u64, points: &Vec<Point>) -> Vec<Point> {
    let mut folded: HashSet<Point> = HashSet::new();

    for p in points {
        if p.y < y {
            folded.insert(Point { x: p.x, y: p.y });
        } else {
            folded.insert(Point {
                x: p.x,
                y: 2 * y - p.y,
            });
        }
    }

    return folded.into_iter().collect();
}
