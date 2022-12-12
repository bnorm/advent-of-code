use std::cmp::Ordering;
use std::collections::{BinaryHeap, HashMap};
use std::hash::Hash;

#[derive(Debug, PartialEq, Eq)]
pub struct SearchNode<T> {
    pub value: T,
    pub cost: usize,
    pub distance: usize,
}

impl<T> SearchNode<T> {
    pub fn new(cost: usize, distance: usize, value: T) -> Self {
        return SearchNode { value, cost, distance };
    }
}

impl<T: Eq + PartialEq> Ord for SearchNode<T> {
    fn cmp(&self, other: &Self) -> Ordering {
        (other.cost + other.distance).cmp(&(self.cost + other.distance))
    }
}

impl<T: PartialEq> PartialOrd for SearchNode<T> {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some((other.cost + other.distance).cmp(&(self.cost + other.distance)))
    }
}

pub fn search<T, F>(
    start: SearchNode<T>,
    neighbors: F,
) -> Option<usize>
    where T: Hash + Eq,
          F: Fn(&SearchNode<T>) -> Vec<SearchNode<T>>
{
    let mut seen = HashMap::<T, usize>::new();
    let mut search = BinaryHeap::<SearchNode<T>>::new();
    search.push(start);

    while let Some(node) = search.pop() {
        if seen.contains_key(&node.value) {
            // The first time we see a node will always be the cheapest ???
            continue
        }

        if node.distance == 0 {
            return Some(node.cost);
        }

        for neighbor in neighbors(&node) {
            search.push(neighbor);
        }

        seen.insert(node.value, node.cost);
    }

    return None;
}
