use std::cmp::Ordering;
use std::collections::{BinaryHeap, HashMap};
use std::hash::Hash;

#[derive(Debug, PartialEq, Eq)]
pub struct SearchNode<T> {
    pub value: T,
    pub cost: isize,
    pub distance: isize,
}

impl<T> SearchNode<T> {
    pub fn new(cost: isize, distance: isize, value: T) -> Self {
        return SearchNode { value, cost, distance };
    }
}

impl<T: Eq + PartialEq> Ord for SearchNode<T> {
    fn cmp(&self, other: &Self) -> Ordering {
        // Reversed to be min-heap
        (other.cost + other.distance).cmp(&(self.cost + self.distance))
    }
}

impl<T: PartialEq> PartialOrd for SearchNode<T> {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        // Reversed to be min-heap
        Some((other.cost + other.distance).cmp(&(self.cost + self.distance)))
    }
}

pub fn search<T, F>(
    start: SearchNode<T>,
    mut neighbors: F,
) -> Option<SearchNode<T>>
    where T: Hash + Eq,
          F: FnMut(&SearchNode<T>) -> Vec<SearchNode<T>>
{
    let mut seen = HashMap::<T, isize>::new();
    let mut search = BinaryHeap::<SearchNode<T>>::new();
    search.push(start);

    while let Some(node) = search.pop() {
        if seen.contains_key(&node.value) && seen[&node.value] <= node.cost + node.distance {
            // We've seen the node before and it cost less before
            continue
        }

        if node.distance == 0 {
            return Some(node);
        }

        let neighbors = neighbors(&node);
        seen.insert(node.value,  node.cost + node.distance);

        for neighbor in neighbors {
            search.push(neighbor);
        }
    }

    return None;
}
