use std::ops::{Index, IndexMut};

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
pub struct Grid<T> {
    values: Vec<Vec<T>>,
    pub num_rows: usize,
    pub num_cols: usize,
}

impl<T> Grid<T> {
    pub fn new(values: Vec<Vec<T>>) -> Self {
        let num_rows = values.len();
        let num_cols = if num_rows == 0 { 0 } else { values[0].len() };
        return Grid { values, num_rows, num_cols };
    }

    pub fn contains(&self, position: &Position) -> bool {
        return position.row < self.num_rows && position.col < self.num_cols;
    }

    pub fn iter(&self) -> impl Iterator<Item=&T> + '_ {
        return self.values.iter().flat_map(|row| row.iter());
    }
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
pub struct Position {
    pub row: usize,
    pub col: usize,
}

impl Position {
    pub fn new(row: usize, col: usize) -> Self {
        Position { row, col }
    }

    pub fn neighbors(&self, num_rows: usize, num_cols: usize) -> impl Iterator<Item=Position> + '_ {
        let mut num = 0;
        return std::iter::from_fn(move || {
            loop {
                num += 1;
                match num {
                    // Up
                    1 => if self.row > 0 {
                        return Some(Position::new(self.row - 1, self.col))
                    },

                    // Down
                    2 => if self.row < num_rows - 1 {
                        return Some(Position::new(self.row + 1, self.col))
                    },

                    // Left
                    3 => if self.col > 0 {
                        return Some(Position::new(self.row, self.col - 1))
                    },

                    // Right
                    4 => if self.col < num_cols - 1 {
                        return Some(Position::new(self.row, self.col + 1))
                    },

                    // Exit
                    _ => return None
                }
            }
        });
    }
}

impl<T> Index<&Position> for Grid<T> {
    type Output = T;

    fn index(&self, position: &Position) -> &Self::Output {
        return &self.values[position.row][position.col];
    }
}

impl<T> IndexMut<&Position> for Grid<T> {
    fn index_mut(&mut self, position: &Position) -> &mut Self::Output {
        return &mut self.values[position.row][position.col];
    }
}
