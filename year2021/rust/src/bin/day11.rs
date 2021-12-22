use std::fs;
use std::time::Instant;

use itertools::Itertools;

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn print(grid: &Vec<Vec<i32>>) {
    let rows = grid.len();
    let columns = grid[0].len();
    for r in 0..rows {
        for c in 0..columns {
            print!("{}", grid[r][c]);
        }
        println!();
    }
    println!();
}

fn part1() -> Option<u64> {
    let start = Instant::now();
    let mut input = read_input()?;
    print(&input);

    let mut result = 0;
    for _ in 0..100 {
        result += step(&mut input);
        // print(&input);
    }

    println!("[part1] time={:?}", start.elapsed());
    return Some(result);
}

fn part2() -> Option<u64> {
    let start = Instant::now();
    let mut input = read_input()?;

    let rows = input.len();
    let columns = input[0].len();

    let mut turn = 0;
    let mut sync = false;
    while !sync {
        step(&mut input);
        turn += 1;
        sync = true;
        for c in 0..columns {
            for r in 0..rows {
                if input[c][r] != 0 {
                    sync = false;
                }
            }
        }
    }

    println!("[part2] time={:?}", start.elapsed());
    return Some(turn);
}

fn read_input() -> Option<Vec<Vec<i32>>> {
    let filename = "res/input11.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let input = contents.lines()
        .map(|line| line.chars()
            .filter_map(|c| c.to_string().parse::<i32>().ok())
            .collect_vec())
        .collect_vec();

    return Some(input);
}

fn step(grid: &mut Vec<Vec<i32>>) -> u64 {
    increase(grid);
    return explode(grid);
}

fn increase(grid: &mut Vec<Vec<i32>>) {
    let rows = grid.len();
    let columns = grid[0].len();
    for c in 0..columns {
        for r in 0..rows {
            grid[r][c] += 1;
        }
    }
}

fn explode(grid: &mut Vec<Vec<i32>>) -> u64 {
    let rows = grid.len();
    let columns = grid[0].len();

    let mut count = 0;
    for c in 0..columns {
        for r in 0..rows {
            count += propagate(grid, c as i32, r as i32);
        }
    }

    for c in 0..columns {
        for r in 0..rows {
            let value = grid[r][c];
            if value == -1 {
                grid[r][c] = 0;
            }
        }
    }

    return count;
}

fn propagate(grid: &mut Vec<Vec<i32>>, ic: i32, ir: i32) -> u64 {
    let rows = grid.len() as i32;
    let columns = grid[0].len() as i32;

    let mut count = 0;
    if grid[ir as usize][ic as usize] > 9 {
        count += 1;
        grid[ir as usize][ic as usize] = -1;

        for nc in (ic - 1).max(0)..(ic + 2).min(columns) {
            for nr in (ir - 1).max(0)..(ir + 2).min(rows) {
                let value = grid[nr as usize][nc as usize];
                if value >= 0 {
                    grid[nr as usize][nc as usize] = value + 1;
                    count += propagate(grid, nc, nr);
                }
            }
        }
    }

    return count;
}
