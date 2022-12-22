const SECTION_SIZE: usize = 50;

pub fn part1() -> String {
    let sections = include_str!("res/input22.txt").split("\n\n").collect::<Vec<_>>();
    let layout = Layout::parse(sections[0]);
    let instructions = Instruction::parse(sections[1].trim());

    let mut section_col = 0;
    loop {
        match layout.grid[0][section_col] {
            None => section_col += 1,
            Some(_) => break,
        }
    }
    let mut position = Position { section_row: 0, section_col: 1, row: 0, col: 0, heading: Heading::East };

    for instruction in instructions {
        match instruction {
            Instruction::Forward(tiles) => {
                for _ in 0..tiles {
                    let next = layout.movement_flat(&position);
                    if next == position {
                        // println!("hit a wall");
                        break
                    }
                    position = next;
                    // println!("move={:?}", position);
                }
            }
            Instruction::Turn(turn_right) => {
                position = layout.turning(&position, turn_right);
                // println!("turn={:?}", position);
            },
        }
    }
    return format!("{:?}", position.password());
}

pub fn part2() -> String {
    let sections = include_str!("res/input22.txt").split("\n\n").collect::<Vec<_>>();
    let layout = Layout::parse(sections[0]);
    let instructions = Instruction::parse(sections[1].trim());

    let mut section_col = 0;
    loop {
        match layout.grid[0][section_col] {
            None => section_col += 1,
            Some(_) => break,
        }
    }
    let mut position = Position { section_row: 0, section_col: 1, row: 0, col: 0, heading: Heading::East };

    for instruction in instructions {
        match instruction {
            Instruction::Forward(tiles) => {
                for _ in 0..tiles {
                    let next = layout.movement_cube(&position);
                    if next == position {
                        // println!("hit a wall");
                        break
                    }
                    position = next;
                    // println!("move={:?}", position);
                }
            }
            Instruction::Turn(turn_right) => {
                position = layout.turning(&position, turn_right);
                // println!("turn={:?}", position);
            },
        }
    }
    return format!("{:?}", position.password());
}

#[derive(Debug)]
enum Instruction {
    Forward(usize),
    Turn(bool /* turn right */),
}

impl Instruction {
    fn parse(input: &str) -> Vec<Instruction> {
        let mut instructions = Vec::<Instruction>::new();

        let mut number = String::new();
        let mut iter = input.chars();
        while let Some(it) = iter.next() {
            match it {
                '0'..='9' => number.push(it),
                'R' => {
                    if number.len() > 0 {
                        instructions.push(Instruction::Forward(number.parse::<usize>().unwrap()));
                        number.clear();
                    }
                    instructions.push(Instruction::Turn(true));
                }
                'L' => {
                    if number.len() > 0 {
                        instructions.push(Instruction::Forward(number.parse::<usize>().unwrap()));
                        number.clear();
                    }
                    instructions.push(Instruction::Turn(false));
                }
                _ => panic!("{}", it),
            }
        }

        if number.len() > 0 {
            instructions.push(Instruction::Forward(number.parse::<usize>().unwrap()));
            number.clear();
        }

        return instructions;
    }
}

#[derive(Debug, Copy, Clone, Eq, PartialEq)]
enum Heading {
    North,
    East,
    South,
    West,
}

#[derive(Debug, Copy, Clone, Eq, PartialEq)]
struct Position {
    pub section_row: usize,
    pub section_col: usize,
    pub row: usize,
    pub col: usize,
    pub heading: Heading,
}

impl Position {
    fn password(&self) -> usize {
        let row = (SECTION_SIZE * self.section_row + self.row) + 1;
        let col = (SECTION_SIZE * self.section_col + self.col) + 1;
        let heading = match self.heading {
            Heading::North => 3,
            Heading::East => 0,
            Heading::South => 1,
            Heading::West => 2,
        };
        return 1000 * row + 4 * col + heading;
    }
}

#[derive(Debug)]
struct Section {
    grid: Vec<Vec<char>>,
}

#[derive(Debug)]
struct Layout {
    grid: Vec<Vec<Option<Section>>>,
    section_rows: usize,
    section_columns: usize,
}

impl Layout {
    fn parse(input: &str) -> Self {
        let mut grid = input.lines().map(|line| line.chars().collect::<Vec<_>>()).collect::<Vec<_>>();
        let max_len = grid.iter().fold(0, |acc, row| acc.max(row.len()));
        for row in &mut grid {
            while row.len() < max_len {
                row.push(' ');
            }
        }

        let mut sections = Vec::<Vec<Option<Section>>>::new();

        let section_rows = grid.len() / SECTION_SIZE;
        let section_columns = max_len / SECTION_SIZE;
        for section_r in 0..section_rows {
            let mut section_row = Vec::<Option<Section>>::new();
            'section: for section_c in 0..section_columns {
                let mut section = Vec::<Vec<char>>::new();
                for r in 0..SECTION_SIZE {
                    let mut row = Vec::<char>::new();
                    for c in 0..SECTION_SIZE {
                        let v = grid[SECTION_SIZE * section_r + r][SECTION_SIZE * section_c + c];
                        if v == ' ' {
                            section_row.push(None);
                            continue 'section;
                        }
                        row.push(v)
                    }
                    section.push(row);
                }
                section_row.push(Some(Section { grid: section }));
            }
            sections.push(section_row);
        }

        return Layout { grid: sections, section_rows, section_columns }
    }

    fn movement_flat(&self, position: &Position) -> Position {
        let section_row = position.section_row;
        let section_col = position.section_col;
        let row = position.row;
        let col = position.col;
        let heading = position.heading;
        let next = match position.heading {
            Heading::North => {
                let (section_row, row) = if row == 0 {
                    let mut section_row = (self.section_rows + section_row - 1) % self.section_rows;
                    loop {
                        match self.grid[section_row][section_col] {
                            None => section_row = (self.section_rows + section_row - 1) % self.section_rows,
                            _ => break,
                        }
                    }
                    (section_row, SECTION_SIZE - 1)
                } else {
                    (section_row, row - 1)
                };
                Position { section_row, section_col, row, col, heading }
            },
            Heading::East => {
                let (section_col, col) = if col == SECTION_SIZE - 1 {
                    let mut section_col = (section_col + 1) % self.section_columns;
                    loop {
                        match self.grid[section_row][section_col] {
                            None => section_col = (section_col + 1) % self.section_columns,
                            _ => break,
                        }
                    }
                    (section_col, 0)
                } else {
                    (section_col, col + 1)
                };
                Position { section_row, section_col, row, col, heading }
            },
            Heading::South => {
                let (section_row, row) = if row == SECTION_SIZE - 1 {
                    let mut section_row = (section_row + 1) % self.section_rows;
                    loop {
                        match self.grid[section_row][section_col] {
                            None => section_row = (section_row + 1) % self.section_rows,
                            _ => break,
                        }
                    }
                    (section_row, 0)
                } else {
                    (section_row, row + 1)
                };
                Position { section_row, section_col, row, col, heading }
            },
            Heading::West => {
                let (section_col, col) = if col == 0 {
                    let mut section_col = (self.section_columns + section_col - 1) % self.section_columns;
                    loop {
                        match self.grid[section_row][section_col] {
                            None => section_col = (self.section_columns + section_col - 1) % self.section_columns,
                            _ => break,
                        }
                    }
                    (section_col, SECTION_SIZE - 1)
                } else {
                    (section_col, col - 1)
                };
                Position { section_row, section_col, row, col, heading }
            },
        };

        return match &self.grid[next.section_row][next.section_col] {
            None => panic!("did not perform movement correctly"),
            Some(section) => {
                if section.grid[next.row][next.col] == '#' {
                    position.clone()
                } else {
                    next
                }
            }
        };
    }

    fn movement_cube(&self, position: &Position) -> Position {
        let section_row = position.section_row;
        let section_col = position.section_col;
        let row = position.row;
        let col = position.col;
        let heading = position.heading;
        let next = match position.heading {
            Heading::North => {
                if row == 0 {
                    match (section_row, section_col) {
                        (0, 1) => Position { section_row: 3, section_col: 0, row: col, col: 0, heading: Heading::East },
                        (0, 2) => Position { section_row: 3, section_col: 0, row: SECTION_SIZE - 1, col, heading },
                        (1, 1) => Position { section_row: 0, section_col: 1, row: SECTION_SIZE - 1, col, heading },
                        (2, 0) => Position { section_row: 1, section_col: 1, row: col, col: 0, heading: Heading::East },
                        (2, 1) => Position { section_row: 1, section_col: 1, row: SECTION_SIZE - 1, col, heading },
                        (3, 0) => Position { section_row: 2, section_col: 0, row: SECTION_SIZE - 1, col, heading },
                        _ => panic!(),
                    }
                } else {
                    Position { section_row, section_col, row: row - 1, col, heading }
                }
            },
            Heading::East => {
                if col == SECTION_SIZE - 1 {
                    match (section_row, section_col) {
                        (0, 1) => Position { section_row: 0, section_col: 2, row, col: 0, heading },
                        (0, 2) => Position { section_row: 2, section_col: 1, row: SECTION_SIZE - row - 1, col: SECTION_SIZE - 1, heading: Heading::West },
                        (1, 1) => Position { section_row: 0, section_col: 2, row: SECTION_SIZE - 1, col: row, heading: Heading::North },
                        (2, 0) => Position { section_row: 2, section_col: 1, row, col: 0, heading },
                        (2, 1) => Position { section_row: 0, section_col: 2, row: SECTION_SIZE - row - 1, col: SECTION_SIZE - 1, heading: Heading::West },
                        (3, 0) => Position { section_row: 2, section_col: 1, row: SECTION_SIZE - 1, col: row, heading: Heading::North },
                        _ => panic!(),
                    }
                } else {
                    Position { section_row, section_col, row, col: col + 1, heading }
                }
            },
            Heading::South => {
                if row == SECTION_SIZE - 1 {
                    match (section_row, section_col) {
                        (0, 1) => Position { section_row: 1, section_col: 1, row: 0, col, heading },
                        (0, 2) => Position { section_row: 1, section_col: 1, row: col, col: SECTION_SIZE - 1, heading: Heading::West },
                        (1, 1) => Position { section_row: 2, section_col: 1, row: 0, col, heading },
                        (2, 0) => Position { section_row: 3, section_col: 0, row: 0, col, heading },
                        (2, 1) => Position { section_row: 3, section_col: 0, row: col, col: SECTION_SIZE - 1, heading: Heading::West },
                        (3, 0) => Position { section_row: 0, section_col: 2, row: 0, col, heading },
                        _ => panic!(),
                    }
                } else {
                    Position { section_row, section_col, row: row + 1, col, heading }
                }
            },
            Heading::West => {
                if col == 0 {
                    match (section_row, section_col) {
                        (0, 1) => Position { section_row: 2, section_col: 0, row: SECTION_SIZE - row - 1, col: 0, heading: Heading::East },
                        (0, 2) => Position { section_row: 0, section_col: 1, row, col: SECTION_SIZE - 1, heading },
                        (1, 1) => Position { section_row: 2, section_col: 0, row: 0, col: row, heading: Heading::South },
                        (2, 0) => Position { section_row: 0, section_col: 1, row: 0, col: SECTION_SIZE - col - 1, heading: Heading::East },
                        (2, 1) => Position { section_row: 2, section_col: 0, row, col: SECTION_SIZE - 1, heading },
                        (3, 0) => Position { section_row: 0, section_col: 1, row: 0, col: row, heading: Heading::South },
                        _ => panic!(),
                    }
                } else {
                    Position { section_row, section_col, row, col: col - 1, heading }
                }
            },
        };

        return match &self.grid[next.section_row][next.section_col] {
            None => panic!("did not perform movement correctly. {:?}->{:?}", position, next),
            Some(section) => {
                if section.grid[next.row][next.col] == '#' {
                    position.clone()
                } else {
                    next
                }
            }
        };
    }

    fn turning(&self, position: &Position, turn_right: bool) -> Position {
        let section_row = position.section_row;
        let section_col = position.section_col;
        let row = position.row;
        let col = position.col;
        if turn_right {
            match position.heading {
                Heading::North => Position { section_row, section_col, row, col, heading: Heading::East },
                Heading::East => Position { section_row, section_col, row, col, heading: Heading::South },
                Heading::South => Position { section_row, section_col, row, col, heading: Heading::West },
                Heading::West => Position { section_row, section_col, row, col, heading: Heading::North },
            }
        } else {
            match position.heading {
                Heading::North => Position { section_row, section_col, row, col, heading: Heading::West },
                Heading::East => Position { section_row, section_col, row, col, heading: Heading::North },
                Heading::South => Position { section_row, section_col, row, col, heading: Heading::East },
                Heading::West => Position { section_row, section_col, row, col, heading: Heading::South },
            }
        }
    }
}
