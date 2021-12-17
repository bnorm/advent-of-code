use std::cmp::max;
use std::fs;
use std::ops::Range;
use std::time::Instant;

use recap::Recap;
use serde::Deserialize;

#[derive(Debug, PartialEq, Eq, Hash, Deserialize, Recap)]
#[recap(regex = r#"target area: x=(?P<x1>-?\d+)..(?P<x2>-?\d+), y=(?P<y1>-?\d+)..(?P<y2>-?\d+)"#)]
struct Input {
    x1: isize,
    x2: isize,
    y2: isize,
    y1: isize,
}

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<isize> {
    let instant = Instant::now();
    let input = read_input();

    let max_y = max(input.y1.abs(), input.y2.abs());

    let mut init_y_vel = 0;
    for y_vel in (0..max_y + 1).rev() {
        if y_intersects(&input, y_vel) {
            init_y_vel = y_vel;
            break;
        }
    }
    let sum = sum(init_y_vel);

    println!("[part1] time={:?}", instant.elapsed());
    return Some(sum);
}

fn part2() -> Option<isize> {
    let instant = Instant::now();
    let input = read_input();

    let max_x = max(input.x1.abs(), input.x2.abs());
    let max_y = max(input.y1.abs(), input.y2.abs());

    let mut count = 0;
    for x_vel in 0..max_x + 1 {
        if sum(x_vel) >= input.x1 {
            for y_vel in -max_y..max_y + 1 {
                if intersects(&input, x_vel, y_vel) { count += 1; }
            }
        }
    }

    println!("[part2] time={:?}", instant.elapsed());
    return Some(count);
}

fn read_input() -> Input {
    let filename = "res/input17.txt";
    let contents = fs::read_to_string(filename).unwrap();
    return contents.trim().parse::<Input>().unwrap();
}

fn sum(x: isize) -> isize {
    let mut sum = 0;
    for v in 1..x + 1 {
        sum += v;
    }
    return sum;
}

fn y_intersects(input: &Input, init_y_vel: isize) -> bool {
    let mut y = 0;
    let mut y_vel = init_y_vel;
    loop {
        y += y_vel;
        y_vel -= 1;
        if y <= input.y2 {
            return y >= input.y1;
        }
    }
}

fn intersects(input: &Input, init_x_vel: isize, init_y_vel: isize) -> bool {
    let mut x = 0;
    let mut y = 0;
    let mut x_vel = init_x_vel;
    let mut y_vel = init_y_vel;

    loop {
        if x_vel == 0 && x < input.x1 { return false; }
        if x > input.x2 || y < input.y1 { return false; }
        if x >= input.x1 && x <= input.x2 && y >= input.y1 && y <= input.y2 { return true; }

        x += x_vel;
        y += y_vel;

        if x_vel > 0 { x_vel -= 1; }
        y_vel -= 1;
    }
}
