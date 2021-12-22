use std::cmp::Ordering;
use std::collections::{BinaryHeap, HashMap};
use std::fs;
use std::time::Instant;

use itertools::Itertools;
use year2021::grid::{Grid, Position};

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
struct Node {
    pos: Position,
    cost: usize,
}

impl Node {
    pub fn new(pos: Position, cost: usize) -> Self {
        Node { pos, cost }
    }
}

impl Ord for Node {
    fn cmp(&self, other: &Self) -> Ordering {
        other.cost.cmp(&self.cost)
    }
}

impl PartialOrd for Node {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<usize> {
    let instant = Instant::now();
    let grid = read_input();

    let cost = calc_path(1, &grid);

    println!("[part1] time={:?}", instant.elapsed());
    return Some(cost);
}

fn part2() -> Option<usize> {
    let instant = Instant::now();
    let grid = read_input();

    let cost = calc_path(5, &grid);

    println!("[part2] time={:?}", instant.elapsed());
    return Some(cost);
}

fn read_input() -> Grid<usize> {
    let filename = "res/input15.txt";
    let contents = fs::read_to_string(filename).unwrap();

    let grid = contents.lines()
        .map(|line| line.chars()
            .map(|c| c.to_string().parse::<usize>().unwrap())
            .collect_vec())
        .collect_vec();

    let num_rows = grid.len();
    let num_cols = grid[0].len();
    return Grid::new(grid, num_rows, num_cols);
}

fn calc_path(multiplier: usize, grid: &Grid<usize>) -> usize {
    let target = Position::new(multiplier * grid.num_rows - 1, multiplier * grid.num_cols - 1);

    let mut node_queue: BinaryHeap<Node> = BinaryHeap::new();

    node_queue.push(Node::new(Position::new(0, 1), calc_cost(grid, &Position::new(0, 1))));
    node_queue.push(Node::new(Position::new(1, 0), calc_cost(grid, &Position::new(1, 0))));

    let mut memory: HashMap<Position, usize> = HashMap::new();
    memory.insert(Position::new(0, 0), 0);

    while let Some(node) = node_queue.pop() {
        if node.pos == target { return node.cost; }

        if let Some(known_cost) = memory.get(&node.pos) {
            if *known_cost <= node.cost { continue; }
        }

        for neighbour in node.pos.neighbours(multiplier * grid.num_rows, multiplier * grid.num_cols) {
            let cost = node.cost + calc_cost(grid, &neighbour);

            if let Some(known_cost) = memory.get(&neighbour) {
                if *known_cost <= cost { continue; }
            }

            node_queue.push(Node::new(neighbour, cost));
            memory.insert(node.pos.clone(), node.cost);
        }
    }

    unreachable!();
}

fn calc_cost(grid: &Grid<usize>, position: &Position) -> usize {
    let grid_cost = grid[&Position::new(position.row % grid.num_rows, position.col % grid.num_cols)];
    let cost = (grid_cost - 1 + (position.row / grid.num_rows) + (position.col / grid.num_cols)) % 9 + 1;
    return cost;
}
