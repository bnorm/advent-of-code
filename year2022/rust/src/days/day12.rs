use year2022::grid::{Grid, Position};
use year2022::search::{search, SearchNode};

pub fn part1() -> String {
    let input: Vec<Vec<char>> = include_str!("res/input12.txt").lines()
        .map(|line| line.chars().collect())
        .collect();

    let grid = Grid::new(input);
    let start = find_char(&grid, 'S').unwrap();
    let end = find_char(&grid, 'E').unwrap();
    let distance_fn = |position: Position| {
        (abs_diff(position.row, end.row) + abs_diff(position.col, end.col)) as isize
    };
    let cost_fn = |node: &SearchNode<_>| { (node.cost + 1) as isize };

    let start_node = SearchNode::new(0, distance_fn(start), Location::new(start, &grid));
    let cost = search(start_node, |node| {
        node.value.position.neighbors(grid.num_rows, grid.num_cols)
            .map(|p| Location::new(p, &grid))
            .filter(|l| l.height as isize - node.value.height as isize <= 1)
            .map(|l| { SearchNode::new(cost_fn(node), distance_fn(l.position), l) })
            .collect()
    }).unwrap().cost;

    return format!("{:?}", cost);
}

pub fn part2() -> String {
    let input: Vec<Vec<char>> = include_str!("res/input12.txt").lines()
        .map(|line| line.chars().collect())
        .collect();

    let grid = Grid::new(input);
    let end = find_char(&grid, 'E').unwrap();
    let distance_fn = |location: Location| { location.height as isize };
    let cost_fn = |node: &SearchNode<_>| { (node.cost + 1) as isize };

    let end_location = Location::new(end, &grid);
    let start_node = SearchNode::new(0, distance_fn(end_location), end_location);
    let cost = search(start_node, |node| {
        node.value.position.neighbors(grid.num_rows, grid.num_cols)
            .map(|p| Location::new(p, &grid))
            .filter(|l| l.height as isize - node.value.height as isize >= -1)
            .map(|l| { SearchNode::new(cost_fn(node), distance_fn(l), l) })
            .collect()
    }).unwrap().cost;

    return format!("{:?}", cost);
}

fn abs_diff(a: usize, b: usize) -> usize {
    return if a > b { a - b } else { b - a };
}

fn find_char(grid: &Grid<char>, value: char) -> Option<Position> {
    for r in 0..grid.num_rows {
        for c in 0..grid.num_cols {
            let p = Position::new(r, c);
            if grid[&p] == value {
                return Some(p);
            }
        }
    }

    return None;
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
struct Location {
    position: Position,
    height: usize,
}

impl Location {
    fn new(position: Position, grid: &Grid<char>) -> Self {
        let value = grid[&position];
        let height = (match value {
            'S' => 'a',
            'E' => 'z',
            _ => value,
        }) as usize - 'a' as usize;
        return Self { position, height };
    }
}
