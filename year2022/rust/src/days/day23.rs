use std::collections::{HashMap, HashSet};
use std::collections::hash_map::Entry;
use std::fmt::{Debug, Formatter};

pub fn part1() -> String {
    let mut grove = Grove::parse(include_str!("res/input23.txt"));
    for _ in 0..10 {
        // println!("=== round {} === heading: {:?}", round, grove.heading);
        // println!("{:?}", grove);
        // println!();

        grove = grove.next();
    }

    // println!("=== round 10 === heading: {:?}", grove.heading);
    // println!("{:?}", grove);
    // println!();

    let (size_x, size_y) = grove.size().unwrap();
    return format!("{:?}", (size_x * size_y - grove.elves.len()));
}

pub fn part2() -> String {
    let mut grove = Grove::parse(include_str!("res/input23.txt"));
    let mut round = 1;
    loop {
        let next = grove.next();
        if next.elves == grove.elves {
            break;
        }
        round += 1;
        grove = next;
    }
    return format!("{:?}", round);
}

struct Grove {
    pub elves: HashSet<Elf>,
    pub heading: Heading,
}

impl Debug for Grove {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        let mut min_x = 0;
        let mut max_x = 13;
        let mut min_y = -11;
        let mut max_y = 0;
        for elf in &self.elves {
            min_x = min_x.min(elf.x - 1);
            max_x = max_x.max(elf.x + 1);
            min_y = min_y.min(elf.y - 1);
            max_y = max_y.max(elf.y + 1);
        }

        for y in (min_y..=max_y).rev() {
            for x in min_x..=max_x {
                if self.elves.contains(&Elf::new(x, y)) {
                    write!(f, "#")?;
                } else {
                    write!(f, ".")?;
                }
            }
            writeln!(f)?;
        }

        return Ok(());
    }
}

impl Grove {
    fn parse(input: &str) -> Self {
        let mut elves = HashSet::<Elf>::new();
        for (y, line) in input.lines().enumerate() {
            for (x, location) in line.chars().enumerate() {
                if location == '#' {
                    elves.insert(Elf::new(x as isize, -(y as isize)));
                }
            }
        }
        return Grove { elves, heading: Heading::North };
    }

    fn size(&self) -> Option<(usize, usize)> {
        let first = self.elves.iter().next()?;
        let mut min_x = first.x;
        let mut max_x = first.x;
        let mut min_y = first.y;
        let mut max_y = first.y;
        for elf in &self.elves {
            min_x = min_x.min(elf.x);
            max_x = max_x.max(elf.x);
            min_y = min_y.min(elf.y);
            max_y = max_y.max(elf.y);
        }
        return Some(((max_x - min_x + 1) as usize, (max_y - min_y + 1) as usize));
    }

    fn next(&self) -> Self {
        // Map of source elves by their proposed destination
        let mut destinations = HashMap::<Elf, Vec<Elf>>::new();

        'elf_loop: for elf in &self.elves {
            let surrounding = elf.surrounding();
            if surrounding.iter().take(8).all(|it| !self.elves.contains(it)) {
                // No surrounding other elves, so do not move
                let destination = elf.clone();
                let source = elf.clone();

                let entry_value = match destinations.entry(destination) {
                    Entry::Occupied(o) => o.into_mut(),
                    Entry::Vacant(v) => v.insert(Vec::new()),
                };
                entry_value.push(source);
            } else {
                // Try to move in one of the ordinal directions
                let mut heading = self.heading;
                for _ in 0..4 {
                    let considerations = &surrounding[2 * heading.ordinal()..(2 * heading.ordinal() + 3)];
                    if considerations.iter().all(|it| !self.elves.contains(it)) {
                        let destination = considerations[1];
                        let source = elf.clone();

                        let entry_value = match destinations.entry(destination) {
                            Entry::Occupied(o) => o.into_mut(),
                            Entry::Vacant(v) => v.insert(Vec::new()),
                        };
                        entry_value.push(source);
                        continue 'elf_loop;
                    }
                    heading = heading.next();
                }

                // If unable, do not move
                let destination = elf.clone();
                let source = elf.clone();

                let entry_value = match destinations.entry(destination) {
                    Entry::Occupied(o) => o.into_mut(),
                    Entry::Vacant(v) => v.insert(Vec::new()),
                };
                entry_value.push(source);
            }
        }

        let mut elves = HashSet::<Elf>::new();
        for (key, value) in destinations {
            if value.len() == 1 {
                elves.insert(key);
            } else {
                elves.extend(value);
            }
        }

        return Grove { elves, heading: self.heading.next() };
    }
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
struct Elf {
    pub x: isize,
    pub y: isize,
}

impl Elf {
    fn new(x: isize, y: isize) -> Self {
        return Elf { x, y };
    }

    fn surrounding(&self) -> [Self; 9] {
        // Repeat NW so that sub-slices can be used for directional considerations
        // NW, N, NE, E, SE, S, SW, W, NW
        return [
            Elf::new(self.x - 1, self.y + 1),
            Elf::new(self.x, self.y + 1),
            Elf::new(self.x + 1, self.y + 1),
            Elf::new(self.x + 1, self.y),
            Elf::new(self.x + 1, self.y - 1),
            Elf::new(self.x, self.y - 1),
            Elf::new(self.x - 1, self.y - 1),
            Elf::new(self.x - 1, self.y),
            Elf::new(self.x - 1, self.y + 1),
        ];
    }
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
enum Heading {
    North,
    East,
    West,
    South,
}

impl Heading {
    fn ordinal(&self) -> usize {
        match self {
            Heading::North => 0,
            Heading::East => 1,
            Heading::South => 2,
            Heading::West => 3,
        }
    }

    fn next(&self) -> Self {
        match self {
            Heading::North => Heading::South,
            Heading::South => Heading::West,
            Heading::West => Heading::East,
            Heading::East => Heading::North,
        }
    }
}
