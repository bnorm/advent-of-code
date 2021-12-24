use std::cmp::Ordering;

#[derive(PartialEq, Eq)]
pub struct SearchNode<T> {
    pub value: T,
    pub dist_start: usize,
    pub dist_end: usize,
}

impl<T: Eq + PartialEq> Ord for SearchNode<T> {
    fn cmp(&self, other: &Self) -> Ordering { (other.dist_start + other.dist_end).cmp(&(self.dist_start + other.dist_end)) }
}

impl<T: PartialEq> PartialOrd for SearchNode<T> {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some((other.dist_start + other.dist_end).cmp(&(self.dist_start + other.dist_end)))
    }
}

impl<T> SearchNode<T> {
    pub fn new(value: T, dist_start: usize, dist_end: usize) -> Self {
        return SearchNode { value, dist_start, dist_end };
    }
}

pub trait HasNeighbors<T = Self> {
    fn neighbors(&self) -> Vec<SearchNode<T>>;
}
