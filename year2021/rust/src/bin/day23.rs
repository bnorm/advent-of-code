use std::cmp::{max, min, Ordering};
use std::collections::{BinaryHeap, HashMap};
use std::fmt::{Debug, Display, Formatter};
use std::fs;
use std::mem::discriminant;
use std::slice::Iter;
use std::time::Instant;

use itertools::Itertools;
use regex::internal::Char;

use year2021::search::{HasNeighbors, SearchNode};

fn abs_diff(a: usize, b: usize) -> usize {
    return if a > b {
        a - b
    } else {
        b - a
    };
}

#[derive(Copy, Clone, PartialEq, Eq, Hash, Debug)]
enum Amphipod {
    Amber,
    Bronze,
    Copper,
    Desert,
}

#[derive(Clone, PartialEq, Eq, Hash, Debug)]
struct Burrow {
    hallway: [Option<Amphipod>; 13],
    rooms: [Room; 4],
}

#[derive(Copy, Clone, PartialEq, Eq, Hash, Debug)]
struct Room {
    amphipod: Amphipod,
    spaces: [Option<Amphipod>; 4],
}

impl Room {
    fn complete(&self) -> bool {
        for d in 0..self.spaces.len() {
            if self.spaces[d] != Some(self.amphipod) { return false; }
        }
        return true;
    }

    fn partial(&self) -> bool {
        for d in 0..self.spaces.len() {
            if self.spaces[d] != Some(self.amphipod) && self.spaces[d] != None { return false; }
        }
        return true;
    }
}

impl Amphipod {
    fn iterator() -> Iter<'static, Amphipod> {
        static DIRECTIONS: [Amphipod; 4] = [Amphipod::Amber, Amphipod::Bronze, Amphipod::Copper, Amphipod::Desert];
        DIRECTIONS.iter()
    }

    fn parse(c: char) -> Option<Amphipod> {
        return match c {
            'A' => Some(Amphipod::Amber),
            'B' => Some(Amphipod::Bronze),
            'C' => Some(Amphipod::Copper),
            'D' => Some(Amphipod::Desert),
            _ => None
        };
    }

    fn movement_cost(&self) -> usize {
        return match self {
            Amphipod::Amber => 1,
            Amphipod::Bronze => 10,
            Amphipod::Copper => 100,
            Amphipod::Desert => 1000,
        };
    }

    fn room_index(&self) -> usize {
        return match self {
            Amphipod::Amber => 0,
            Amphipod::Bronze => 1,
            Amphipod::Copper => 2,
            Amphipod::Desert => 3,
        };
    }

    fn target_column(&self) -> usize {
        return match self {
            Amphipod::Amber => 3,
            Amphipod::Bronze => 5,
            Amphipod::Copper => 7,
            Amphipod::Desert => 9,
        };
    }
}

type Input = Burrow;

fn main() {
    let start = Instant::now();
    let input = read_input("res/input23.txt");
    println!("[part1] answer={:?}", part1(&input));
    println!("[part2] answer={:?}", part2(&input));
    println!("finished in {:?}", start.elapsed());
}

fn read_input(file: &str) -> Input {
    let filename = file;
    let contents = fs::read_to_string(filename).unwrap();
    let input = contents.lines().map(|line| line.chars().collect_vec()).collect_vec();

    let mut hallway = [None; 13];
    for c in 0..input[1].len() {
        hallway[c] = Amphipod::parse(input[1][c]);
    }

    let mut rooms = [Amphipod::Amber, Amphipod::Bronze, Amphipod::Copper, Amphipod::Desert]
        .map(|a| Room { amphipod: a, spaces: [None; 4] });
    for room in &mut rooms {
        let c = room.amphipod.target_column();
        for r in [2, 3, 4, 5] {
            let parse = Amphipod::parse(input[r][c]);
            room.spaces[r - 2] = parse;
        }
    }

    return Burrow { hallway, rooms };
}

fn part1(input: &Input) -> Option<usize> {
    let instant = Instant::now();

    // let start = read_input("res/input23_start.txt");
    // let start_node = Node::new(start.clone(), 0);
    // println!("cost=0 dist={} total={}", start_node.dist, start_node.cost + start_node.dist);
    // println!("{}", start);
    // println!();
    //
    // let mut search = BinaryHeap::<Node>::new();
    // for (state, cost) in start.possible_moves() {
    //     let node = Node::new(state.clone(), cost);
    //     search.push(node);
    // }
    //
    // while let Some(node) = search.pop() {
    //     println!("cost={} dist={} total={}", node.cost, node.dist, node.cost + node.dist);
    //     println!("{}", node.state);
    //     println!();
    // }
    //
    // return None;

    let mut search = BinaryHeap::<Node>::new();
    search.push(Node::new(input.clone(), 0));

    let mut count = 0 as usize;
    while let Some(node) = search.pop() {
        if node.state.complete() {
            print!("{}", node.state);
            return Some(node.cost);
        }

        count += 1;
        if count % 100000 == 0 {
            println!("queue_size={}", search.len());
            println!("cost={} dist={}", node.cost, node.dist);
            print!("{}", node.state);
            println!();
        }

        for (state, move_cost) in node.state.possible_moves() {
            let cost = node.cost + move_cost;
            search.push(Node::new(state, cost));
        }
    }

    println!("[part1] time={:?}", instant.elapsed());
    return None;
}

fn part2(input: &Input) -> Option<usize> {
    let instant = Instant::now();

    println!("[part2] time={:?}", instant.elapsed());
    return None;
}

#[derive(PartialEq, Eq)]
struct Node {
    state: Burrow,
    cost: usize,
    dist: usize,
}

impl Node {
    pub fn new(state: Burrow, cost: usize) -> Self {
        let mut dist = 0usize;

        for column in 0..state.rooms.len() {
            let room = state.rooms[column];
            let mut trapped = false;
            for depth in (0..room.spaces.len()).rev() {
                if let Some(amphipod) = room.spaces[depth] {
                    if amphipod != room.amphipod {
                        trapped = true;
                        dist += (depth + 1) * amphipod.movement_cost(); // move out
                        dist += (depth + 1) * room.amphipod.movement_cost(); // something needs to move in
                        dist += abs_diff(room.amphipod.target_column(), amphipod.target_column()) * amphipod.movement_cost(); // move laterally
                    } else if trapped {
                        dist += 2 * (depth + 1) * amphipod.movement_cost(); // move out and back in
                        dist += 2 * amphipod.movement_cost(); // move out of the way
                    }
                } else {
                    dist += (depth + 1) * room.amphipod.movement_cost(); // something needs to move in
                }
            }
        }

        for column in 0..state.hallway.len() {
            if let Some(amphipod) = state.hallway[column] {
                dist += abs_diff(column, amphipod.target_column()) * amphipod.movement_cost(); // move laterally
            }
        }

        return Node { state, cost, dist };
    }
}

impl Ord for Node {
    fn cmp(&self, other: &Self) -> Ordering {
        (other.cost + other.dist).cmp(&(self.cost + self.dist))
    }
}

impl PartialOrd for Node {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

impl Display for Burrow {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        fn write_space(f: &mut Formatter<'_>, amphipod: &Option<Amphipod>, default: &str) -> std::fmt::Result {
            return if let Some(amphipod) = &amphipod {
                write!(f, "{}", amphipod)
            } else {
                write!(f, "{}", default)
            };
        }

        writeln!(f, "#############")?;
        write!(f, "#")?;
        for c in 1..=11 {
            write_space(f, &self.hallway[c], ".")?;
        }
        writeln!(f, "#")?;

        write!(f, "###")?;
        for c in 3..=9 {
            if c % 2 == 1 {
                write_space(f, &self.rooms[(c - 3) / 2].spaces[0], ".")?;
            } else {
                write!(f, "#")?;
            }
        }
        writeln!(f, "###")?;

        write!(f, "  #")?;
        for c in 3..=9 {
            if c % 2 == 1 {
                write_space(f, &self.rooms[(c - 3) / 2].spaces[1], ".")?;
            } else {
                write!(f, "#")?;
            }
        }
        writeln!(f, "#  ")?;

        write!(f, "  #")?;
        for c in 3..=9 {
            if c % 2 == 1 {
                write_space(f, &self.rooms[(c - 3) / 2].spaces[2], ".")?;
            } else {
                write!(f, "#")?;
            }
        }
        writeln!(f, "#  ")?;

        write!(f, "  #")?;
        for c in 3..=9 {
            if c % 2 == 1 {
                write_space(f, &self.rooms[(c - 3) / 2].spaces[3], ".")?;
            } else {
                write!(f, "#")?;
            }
        }
        writeln!(f, "#  ")?;

        writeln!(f, "  #########  ")?;

        Ok(())
    }
}


impl Display for Amphipod {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        return match self {
            Amphipod::Amber => write!(f, "A"),
            Amphipod::Bronze => write!(f, "B"),
            Amphipod::Copper => write!(f, "C"),
            Amphipod::Desert => write!(f, "D"),
        };
    }
}

impl HasNeighbors for Burrow {
    fn neighbors(&self) -> Vec<SearchNode<Burrow>> {
        todo!()
    }
}

impl Burrow {
    fn complete(&self) -> bool {
        for room in self.rooms {
            if !room.complete() { return false; }
        }
        return true;
    }

    fn possible_moves(&self) -> Vec<(Burrow, usize)> {
        let mut possible = Vec::<(Burrow, usize)>::new();
        let hallway_columns = [1, 2, 4, 6, 8, 10, 11];

        // Move a piece out from room
        for column in Amphipod::iterator() {
            let start_c = column.target_column();
            let room = self.rooms[column.room_index()];
            if !room.partial() {
                for depth in 0..room.spaces.len() {
                    if let Some(amphipod) = room.spaces[depth] {
                        for end_c in hallway_columns {
                            if !self.hallway_blocked(start_c, end_c) {
                                let mut new_state = self.clone();
                                new_state.hallway[end_c] = Some(amphipod);
                                new_state.rooms[column.room_index()].spaces[depth] = None;
                                possible.push((new_state, (depth + 1 + abs_diff(end_c, start_c)) * amphipod.movement_cost()));
                            }
                        }

                        break; // can only move the top-most amphipod
                    }
                }
            }
        }

        // Move a piece in from hallway to room
        for start_c in hallway_columns {
            if let Some(amphipod) = self.hallway[start_c] {
                let end_c = amphipod.target_column();
                if !self.hallway_blocked(start_c, end_c) {
                    let target_room = self.rooms[amphipod.room_index()];
                    for end_depth in (0..target_room.spaces.len()).rev() {
                        if target_room.spaces[end_depth] == None {
                            let mut new_state = self.clone();
                            new_state.hallway[start_c] = None;
                            new_state.rooms[amphipod.room_index()].spaces[end_depth] = Some(amphipod);
                            possible.push((new_state, (end_depth + 1 + abs_diff(end_c, start_c)) * amphipod.movement_cost()));
                            break; // must always move to the bottom
                        } else if let Some(neighbor) = target_room.spaces[end_depth] {
                            if neighbor != amphipod {
                                break; // Someone still needs to move out
                            }
                        }
                    }
                }
            }
        }

        return possible;
    }

    fn hallway_blocked(&self, start_c: usize, end_c: usize) -> bool {
        for c in min(start_c, end_c) + 1..=max(start_c, end_c) - 1 {
            if &self.hallway[c] != &None { return true; }
        }
        if &self.hallway[end_c] != &None { return true; }
        return false;
    }
}
