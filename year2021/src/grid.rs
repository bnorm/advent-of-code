use std::iter::{from_fn, FromFn};
use std::ops::Index;
use std::slice::Iter;

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
pub struct Grid<T> {
    values: Vec<Vec<T>>,
    pub num_rows: usize,
    pub num_cols: usize,
}

impl<T> Grid<T> {
    pub fn new(values: Vec<Vec<T>>, num_rows: usize, num_cols: usize) -> Self {
        return Grid { values, num_rows, num_cols };
    }
}

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
pub struct Position {
    pub row: usize,
    pub col: usize,
}

impl Position {
    pub fn new(row: usize, col: usize) -> Self {
        Position { row, col }
    }

    pub fn neighbours(&self, num_rows: usize, num_cols: usize) -> Vec<Position> {
        let mut result = Vec::new();

        // Up
        if self.row > 0 {
            result.push(Position::new(self.row - 1, self.col));
        }

        // Down
        if self.row < num_rows - 1 {
            result.push(Position::new(self.row + 1, self.col));
        }

        // Left
        if self.col > 0 {
            result.push(Position::new(self.row, self.col - 1));
        }

        // Right
        if self.col < num_cols - 1 {
            result.push(Position::new(self.row, self.col + 1));
        }

        return result;
    }
}

impl<T> Index<&Position> for Grid<T> {
    type Output = T;

    fn index(&self, position: &Position) -> &Self::Output {
        return &self.values[position.row][position.col];
    }
}
