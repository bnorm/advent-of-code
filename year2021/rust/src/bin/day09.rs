use std::collections::{HashSet, VecDeque};
use std::fs;
use std::time::Instant;

use itertools::Itertools;

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u64> {
    let start = Instant::now();
    let input = read_input()?;

    let rows = input.len();
    let columns = input[0].len();

    let mut result = 0;
    for c in 0..columns {
        for r in 0..rows {
            result += risk_level(&input, r, c);
        }
    }

    println!("[part1] time={:?}", start.elapsed());
    return Some(result);
}

fn part2() -> Option<u64> {
    let start = Instant::now();
    let input = read_input()?;

    let rows = input.len();
    let columns = input[0].len();

    let mut sizes = vec![];
    for c in 0..columns {
        for r in 0..rows {
            if risk_level(&input, r, c) > 0 {
                let size = basin_size(&input, r, c);
                // println!("r={} c={} size={}", r, c, size);
                sizes.push(size);
            }
        }
    }

    sizes.sort();
    sizes.reverse();
    println!("sizes={:?}", sizes);
    let result = sizes[0] * sizes[1] * sizes[2];

    println!("[part2] time={:?}", start.elapsed());
    return Some(result);
}

fn read_input() -> Option<Vec<Vec<u8>>> {
    let filename = "res/input09.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let input = contents
        .lines()
        .map(|line| {
            line.chars()
                .filter_map(|c| c.to_string().parse::<u8>().ok())
                .collect_vec()
        })
        .collect_vec();

    return Some(input);
}

fn risk_level(grid: &Vec<Vec<u8>>, r: usize, c: usize) -> u64 {
    let rows = grid.len();
    let columns = grid[0].len();
    let value = grid[r][c];

    if r > 0 && grid[r - 1][c] <= value {
        return 0;
    }
    if r < rows - 1 && grid[r + 1][c] <= value {
        return 0;
    }

    if c > 0 && grid[r][c - 1] <= value {
        return 0;
    }
    if c < columns - 1 && grid[r][c + 1] <= value {
        return 0;
    }

    return 1 + value as u64;
}

fn basin_size(grid: &Vec<Vec<u8>>, sr: usize, sc: usize) -> u64 {
    let mut basin: HashSet<(usize, usize)> = HashSet::new();
    let mut queue: VecDeque<(usize, usize)> = VecDeque::new();

    let rows = grid.len();
    let columns = grid[0].len();

    queue.push_back((sr, sc));

    while queue.len() > 0 {
        let (r, c) = queue.pop_front().unwrap();
        let value = grid[r][c];
        if value < 9 && basin.insert((r, c)) {
            if r > 0 && grid[r - 1][c] > value {
                queue.push_back((r - 1, c));
            }
            if r < rows - 1 && grid[r + 1][c] > value {
                queue.push_back((r + 1, c))
            }

            if c > 0 && grid[r][c - 1] > value {
                queue.push_back((r, c - 1));
            }
            if c < columns - 1 && grid[r][c + 1] > value {
                queue.push_back((r, c + 1));
            }
        }
    }

    return basin.len() as u64;
}
