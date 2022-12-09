use year2022::grid::{Grid, Position};

pub fn part1() -> String {
    let input = include_str!("res/input08.txt").lines().collect::<Vec<_>>();

    let grid = input.iter()
        .map(|line| {
            line.chars()
                .map(|c| c.to_string().parse::<usize>().unwrap())
                .collect::<Vec<_>>()
        })
        .collect::<Vec<_>>();
    let num_rows = grid.len();
    let num_cols = grid[0].len();

    let count = count(&Grid::new(grid, num_rows, num_cols));

    return format!("{:?}", count);
}

pub fn part2() -> String {
    let input = include_str!("res/input08.txt").lines().collect::<Vec<_>>();

    let grid = input.iter()
        .map(|line| {
            line.chars()
                .map(|c| c.to_string().parse::<usize>().unwrap())
                .collect::<Vec<_>>()
        })
        .collect::<Vec<_>>();
    let num_rows = grid.len();
    let num_cols = grid[0].len();

    let scenic = most_scenic(&Grid::new(grid, num_rows, num_cols));

    return format!("{:?}", scenic);
}

fn count(grid: &Grid<usize>) -> usize {
    let mut count = 0;

    for r in 0..grid.num_rows {
        for c in 0..grid.num_cols {
            if check_visibility(grid, &Position::new(r, c)) {
                count += 1;
            }
        }
    }

    return count;
}

fn most_scenic(grid: &Grid<usize>) -> usize {
    let mut scenic = 0;

    for r in 0..grid.num_rows {
        for c in 0..grid.num_cols {
            let s = check_scenic(grid, &Position::new(r, c));
            if s > scenic {
                scenic = s;
            }
        }
    }

    return scenic;
}

fn check_visibility(grid: &Grid<usize>, p: &Position) -> bool {
    if p.row == 0 || p.col == 0 || p.row == grid.num_rows - 1 || p.col == grid.num_cols - 1 {
        return true
    }

    let height = grid[p];

    let mut up = true;
    for r in 0..p.row {
        if grid[&Position::new(r, p.col)] >= height {
            up = false
        }
    }
    if up { return true; }

    let mut down = true;
    for r in p.row + 1..grid.num_rows {
        if grid[&Position::new(r, p.col)] >= height {
            down = false;
        }
    }
    if down { return true; }

    let mut left = true;
    for c in 0..p.col {
        if grid[&Position::new(p.row, c)] >= height {
            left = false
        }
    }
    if left { return true; }

    let mut right = true;
    for c in p.col + 1..grid.num_cols {
        if grid[&Position::new(p.row, c)] >= height {
            right = false
        }
    }
    if right { return true; }

    return false;
}

fn check_scenic(grid: &Grid<usize>, p: &Position) -> usize {
    if p.row == 0 || p.col == 0 || p.row == grid.num_rows - 1 || p.col == grid.num_cols - 1 {
        return 0
    }

    let height = grid[p];

    let mut up = 0;
    for r in (0..p.row).rev() {
        up += 1;
        if grid[&Position::new(r, p.col)] >= height {
            break;
        }
    }

    let mut down = 0;
    for r in p.row + 1..grid.num_rows {
        down += 1;
        if grid[&Position::new(r, p.col)] >= height {
            break;
        }
    }

    let mut left = 0;
    for c in (0..p.col).rev() {
        left += 1;
        if grid[&Position::new(p.row, c)] >= height {
            break;
        }
    }

    let mut right = 0;
    for c in p.col + 1..grid.num_cols {
        right += 1;
        if grid[&Position::new(p.row, c)] >= height {
            break;
        }
    }

    return up * down * left * right;
}
