use std::collections::HashSet;

use recap::Recap;
use serde::Deserialize;

pub fn part1() -> String {
    let input = include_str!("res/input09.txt").lines().collect::<Vec<_>>();
    let instructions = input.iter().map(|line| { line.parse::<Instruction>().unwrap() }).collect::<Vec<_>>();

    let count = simulate_rope(instructions, 2);
    return format!("{:?}", count);
}

pub fn part2() -> String {
    let input = include_str!("res/input09.txt").lines().collect::<Vec<_>>();
    let instructions = input.iter().map(|line| { line.parse::<Instruction>().unwrap() }).collect::<Vec<_>>();

    let count = simulate_rope(instructions, 10);
    return format!("{:?}", count);
}

fn simulate_rope(instructions: Vec<Instruction>, knot_count: usize) -> usize {
    let mut visited = HashSet::<Point>::new();
    let mut knots = Vec::<Point>::new();
    for _ in 0..knot_count {
        knots.push(Point::new(0, 0));
    }

    for instruction in instructions {
        for _ in 0..instruction.distance {
            match instruction.direction {
                'R' => {
                    knots[0].col += 1;
                },
                'L' => {
                    knots[0].col -= 1;
                },
                'U' => {
                    knots[0].row += 1;
                },
                'D' => {
                    knots[0].row -= 1;
                },
                _ => {},
            }
            for i in 0..knot_count - 1 {
                let (first, second) = knots.split_at_mut(i + 1);
                tail_follow(&first[first.len() - 1], &mut second[0]);
            }
            visited.insert(knots[knots.len() - 1].clone());
        }
    }

    return visited.len();
}

#[derive(Debug, Clone, Hash, Eq, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"(?P<direction>[LRUD]) (?P<distance>\d+)"#)]
struct Instruction {
    pub direction: char,
    pub distance: usize,
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
pub struct Point {
    pub row: isize,
    pub col: isize,
}

impl Point {
    pub fn new(row: isize, col: isize) -> Self {
        Point { row, col }
    }
}

fn tail_follow(head: &Point, tail: &mut Point) {
    if head.row > tail.row {
        if head.row - tail.row > 1 {
            tail.row += 1;
            if head.col > tail.col {
                tail.col += 1;
            } else if head.col < tail.col {
                tail.col -= 1;
            }
            return;
        }
    } else if head.row < tail.row {
        if tail.row - head.row > 1 {
            tail.row -= 1;
            if head.col > tail.col {
                tail.col += 1;
            } else if head.col < tail.col {
                tail.col -= 1;
            }
            return;
        }
    }

    if head.col > tail.col {
        if head.col - tail.col > 1 {
            tail.col += 1;
            if head.row > tail.row {
                tail.row += 1;
            } else if head.row < tail.row {
                tail.row -= 1;
            }
            return;
        }
    } else if head.col < tail.col {
        if tail.col - head.col > 1 {
            tail.col -= 1;
            if head.row > tail.row {
                tail.row += 1;
            } else if head.row < tail.row {
                tail.row -= 1;
            }
            return;
        }
    }
}
