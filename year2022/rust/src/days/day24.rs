use std::collections::HashSet;

use year2022::search::{search, SearchNode};

pub fn part1() -> String {
    let lines = include_str!("res/input24.txt").lines().collect::<Vec<_>>();

    let valley = Valley::parse(lines);
    let start = Position { row: -1, col: 0 };
    let target = Position { row: valley.rows, col: valley.cols - 1 };

    let result = search_part1(&start, &target, valley);
    return format!("{:?}", result);
}

pub fn part2() -> String {
    let lines = include_str!("res/input24.txt").lines().collect::<Vec<_>>();

    let valley = Valley::parse(lines);
    let start = Position { row: -1, col: 0 };
    let target = Position { row: valley.rows, col: valley.cols - 1 };

    let result = search_part2(&start, &target, valley);
    return format!("{:?}", result);
}

fn distance(position: &Position, target: &Position) -> isize {
    return (target.row - position.row).abs() + (target.col - position.col).abs();
}

fn get_valley(time: usize, valleys: &mut Vec<Valley>) -> &Valley {
    while valleys.len() <= time {
        let next = valleys[valleys.len() - 1].next();
        valleys.push(next);
    }
    return &valleys[time];
}

fn find_path(start: &Position, target: &Position, mut valleys: &mut Vec<Valley>, time: usize) -> Option<SearchNode<You>> {
    let start_node = SearchNode::new(time as isize, distance(start, target), You::new(start.clone(), time));
    return search(start_node, |node| {
        let time = node.cost + 1;
        let valley = get_valley(time as usize, &mut valleys);
        let nodes = node.value.movement(valley)
            .filter(|you| !valley.blizzard_positions.contains(&you.position))
            .map(|you| SearchNode::new(time, distance(&you.position, target), you))
            .collect();

        // println!("node     = {:?}", node);
        // for n in &nodes { println!("movement = {:?}", n); }
        // println!();

        return nodes;
    });
}

fn search_part1(start: &Position, target: &Position, valley: Valley) -> usize {
    let mut valleys = Vec::<Valley>::new();
    valleys.push(valley);

    return find_path(start, target, &mut valleys, 0).unwrap().cost as usize;
}

fn search_part2(start: &Position, target: &Position, valley: Valley) -> usize {
    let mut valleys = Vec::<Valley>::new();
    valleys.push(valley);

    // there, back, and there again
    let end_time_1 = find_path(start, target, &mut valleys, 0).unwrap().cost as usize;
    let end_time_2 = find_path(target, start, &mut valleys, end_time_1).unwrap().cost as usize;
    let end_time_3 = find_path(start, target, &mut valleys, end_time_2).unwrap().cost as usize;

    return end_time_3;
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
struct You {
    pub position: Position,
    pub time: usize,
}

impl You {
    fn new(position: Position, time: usize) -> Self {
        return Self { position, time };
    }

    fn movement<'a>(&'a self, valley: &'a Valley) -> impl Iterator<Item=You> + 'a {
        let mut num = 0;
        let time = self.time + 1;
        return std::iter::from_fn(move || {
            loop {
                num += 1;
                match num {
                    1 => if self.position.row - 1 >= 0 || self.position.col == 0 {
                        return Some(You::new(Position::new(self.position.row - 1, self.position.col), time)); // Up
                    },
                    2 => if self.position.col + 1 < valley.cols && self.position.row >= 0 && self.position.row < valley.rows {
                        return Some(You::new(Position::new(self.position.row, self.position.col + 1), time)); // Right
                    },
                    3 => if self.position.row + 1 < valley.rows || self.position.col == valley.cols - 1 {
                        return Some(You::new(Position::new(self.position.row + 1, self.position.col), time)); // Down
                    },
                    4 => if self.position.col - 1 >= 0 && self.position.row >= 0 && self.position.row < valley.rows {
                        return Some(You::new(Position::new(self.position.row, self.position.col - 1), time)); // Left
                    },
                    5 => return Some(You::new(Position::new(self.position.row, self.position.col), time)), // Wait
                    _ => return None, // Exit
                }
            }
        });
    }
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
struct Position {
    pub row: isize,
    pub col: isize,
}

impl Position {
    fn new(row: isize, col: isize) -> Self {
        return Self { row, col };
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
struct Valley {
    pub rows: isize,
    pub cols: isize,
    pub blizzards: HashSet<Blizzard>,
    pub blizzard_positions: HashSet<Position>,
}

impl Valley {
    fn parse(input: Vec<&str>) -> Self {
        let rows: isize = input.len() as isize - 2;
        let cols: isize = input[0].len() as isize - 2;
        let mut blizzards = HashSet::new();
        for (r, line) in input.iter().enumerate() {
            for (c, char) in line.chars().enumerate() {
                match char {
                    '^' => blizzards.insert(Blizzard::new(Position::new(r as isize - 1, c as isize - 1), Direction::Up)),
                    '>' => blizzards.insert(Blizzard::new(Position::new(r as isize - 1, c as isize - 1), Direction::Right)),
                    'v' => blizzards.insert(Blizzard::new(Position::new(r as isize - 1, c as isize - 1), Direction::Down)),
                    '<' => blizzards.insert(Blizzard::new(Position::new(r as isize - 1, c as isize - 1), Direction::Left)),
                    _ => false,
                };
            }
        }
        return Self::new(rows, cols, blizzards);
    }

    fn new(rows: isize, cols: isize, blizzards: HashSet<Blizzard>) -> Self {
        let blizzard_positions = blizzards.iter().map(|b| b.position.clone()).collect();
        return Self { rows, cols, blizzards, blizzard_positions };
    }

    fn next(&self) -> Self {
        return Valley::new(self.rows, self.cols, self.blizzards.iter().map(|b| b.next(self)).collect());
    }
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
struct Blizzard {
    pub position: Position,
    pub direction: Direction,
}

impl Blizzard {
    fn new(position: Position, direction: Direction) -> Self {
        return Self { position, direction };
    }

    fn next(&self, valley: &Valley) -> Self {
        let position = match self.direction {
            Direction::Up => {
                let row = if self.position.row <= 0 { valley.rows - 1 } else { self.position.row - 1 };
                Position::new(row, self.position.col)
            }
            Direction::Right => {
                let col = if self.position.col + 1 >= valley.cols { 0 } else { self.position.col + 1 };
                Position::new(self.position.row, col)
            }
            Direction::Down => {
                let row = if self.position.row + 1 >= valley.rows { 0 } else { self.position.row + 1 };
                Position::new(row, self.position.col)
            }
            Direction::Left => {
                let col = if self.position.col <= 0 { valley.cols - 1 } else { self.position.col - 1 };
                Position::new(self.position.row, col)
            }
        };
        return Blizzard::new(position, self.direction)
    }
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
enum Direction {
    Up,
    Right,
    Down,
    Left,
}
