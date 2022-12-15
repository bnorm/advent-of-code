use recap::Recap;
use serde::Deserialize;

use year2022::grid::{Grid, Position};

pub fn part1() -> String {
    let paths = include_str!("res/input14.txt").lines()
        .map(|line| line.split(" -> ")
            .map(|point| point.parse::<Point>().unwrap())
            .collect::<Vec<_>>())
        .collect::<Vec<_>>();

    let mut wall = build_wall(&paths);

    let count = simulate(&mut wall);
    return format!("{:?}", count);
}

pub fn part2() -> String {
    let paths = include_str!("res/input14.txt").lines()
        .map(|line| line.split(" -> ")
            .map(|point| point.parse::<Point>().unwrap())
            .collect::<Vec<_>>())
        .collect::<Vec<_>>();

    let mut wall = build_wall(&paths);

    // Add infinite line
    let max_y = paths.iter().fold(0, |acc, path| acc.max(path.iter().fold(0, |acc, point| acc.max(point.y))));
    for x in 0..1000 {
        wall[&Position::new(max_y + 2, x)] = '#';
    }

    let count = simulate(&mut wall);
    return format!("{:?}", count);
}

#[derive(Debug, Copy, Clone, Deserialize, Recap)]
#[recap(regex = r#"(?P<x>\d+),(?P<y>\d+)"#)]
struct Point {
    pub x: usize,
    pub y: usize,
}

#[allow(dead_code)]
fn display_wall(grid: &Grid<char>, row_start: usize, row_end: usize, col_start: usize, col_end: usize) {
    for row in row_start..row_end {
        for col in col_start..col_end {
            print!("{}", grid[&Position::new(row, col)])
        }
        println!()
    }
    println!()
}

fn build_wall(paths: &Vec<Vec<Point>>) -> Grid<char> {
    let mut wall = Grid::new(vec![vec!['.'; 1000]; 1000]);
    for path in paths {
        let mut iter = path.iter();
        let mut prev = iter.next().unwrap();
        let mut next = iter.next().unwrap();
        loop {
            if prev.x == next.x {
                for y in prev.y.min(next.y)..=prev.y.max(next.y) {
                    wall[&Position::new(y, next.x)] = '#'
                }
            } else if prev.y == next.y {
                for x in prev.x.min(next.x)..=prev.x.max(next.x) {
                    wall[&Position::new(next.y, x)] = '#'
                }
            }
            if let Some(it) = iter.next() {
                prev = next;
                next = it;
            } else {
                break;
            }
        }
    }

    return wall;
}

fn simulate(wall: &mut Grid<char>) -> i32 {
    let drip = "500,0".parse::<Point>().unwrap();
    let mut count = 0;
    loop {
        if wall[&Position::new(drip.y, drip.x)] == 'o' {
            break;
        }
        // display_wall(&wall, 0, 14, 480, 515);

        let mut sand = drip.clone();
        while sand.y < 1000 {
            if wall[&Position::new(sand.y, sand.x)] != '.' {
                if wall[&Position::new(sand.y, sand.x - 1)] == '.' {
                    sand.x -= 1;
                } else if wall[&Position::new(sand.y, sand.x + 1)] == '.' {
                    sand.x += 1;
                } else {
                    wall[&Position::new(sand.y - 1, sand.x)] = 'o';
                    count += 1;
                    break;
                }
            }

            sand.y += 1;
        }

        if sand.y >= 1000 {
            break
        }
    }
    // display_wall(&wall, 0, 14, 480, 515);

    return count;
}
