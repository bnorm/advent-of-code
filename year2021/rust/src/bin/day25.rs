use std::fs;
use std::time::Instant;

use itertools::Itertools;

use year2021::grid::{Grid, Position};

type Input = Grid<char>;

fn main() {
    let start = Instant::now();
    let input = read_input("res/input25.txt");
    println!("[part1] answer={:?}", part1(&input));
    println!("[part2] answer={:?}", part2(&input));
    println!("finished in {:?}", start.elapsed());
}

fn read_input(file: &str) -> Input {
    let filename = file;
    let contents = fs::read_to_string(filename).unwrap();

    let grid = contents
        .lines()
        .map(|line| line.chars().collect_vec())
        .collect_vec();

    let num_rows = grid.len();
    let num_cols = grid[0].len();
    return Grid::new(grid, num_rows, num_cols);
}

fn part1(_input: &Input) -> Option<usize> {
    let instant = Instant::now();

    let mut grid = _input.clone();
    let mut turns = 1;
    while step(&mut grid) {
        turns += 1;
        // println!("turns={}", turns);
        display(&grid);
    }

    println!("turns={}", turns);

    println!("[part1] time={:?}", instant.elapsed());
    return None;
}

fn part2(_input: &Input) -> Option<usize> {
    let instant = Instant::now();

    println!("[part2] time={:?}", instant.elapsed());
    return None;
}

fn step(grid: &mut Grid<char>) -> bool {
    let mut moved = false;

    // Step right
    for r in 0..grid.num_rows {
        for c in (0..grid.num_cols).rev() {
            let start = Position::new(r, c);
            let end = Position::new(r, (c + 1) % grid.num_cols);
            if grid[&start] == '>' && grid[&end] == '.' {
                // println!("({},{})->({},{})", start.row, start.col, end.row, end.col);
                grid[&end] = 'h';
                grid[&start] = 't';
                moved = true;
            }
        }
    }

    // clear cucumber trails
    for r in 0..grid.num_rows {
        for c in 0..grid.num_cols {
            let start = Position::new(r, c);
            if grid[&start] == 't' {
                grid[&start] = '.';
            } else if grid[&start] == 'h' {
                grid[&start] = '>';
            }
        }
    }

    // Step down
    for c in 0..grid.num_cols {
        for r in (0..grid.num_rows).rev() {
            let start = Position::new(r, c);
            let end = Position::new((r + 1) % grid.num_rows, c);
            if grid[&start] == 'v' && grid[&end] == '.' {
                // println!("({},{})->({},{})", start.row, start.col, end.row, end.col);
                grid[&end] = 'h';
                grid[&start] = 't';
                moved = true;
            }
        }
    }

    // clear cucumber trails
    for r in 0..grid.num_rows {
        for c in 0..grid.num_cols {
            let start = Position::new(r, c);
            if grid[&start] == 't' {
                grid[&start] = '.';
            } else if grid[&start] == 'h' {
                grid[&start] = 'v';
            }
        }
    }

    return moved;
}

fn display(grid: &Grid<char>) {
    // println!("{}x{}", grid.num_rows, grid.num_cols);
    for r in 0..grid.num_rows {
        for c in 0..grid.num_cols {
            print!("{}", grid[&Position::new(r, c)]);
        }
        println!();
    }
    println!();
}
