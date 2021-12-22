use std::collections::VecDeque;
use std::ops::{Index, IndexMut, Range, RangeInclusive};

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
pub struct Cube<T> {
    values: Vec<Vec<Vec<T>>>,
    pub x_range: Range<isize>,
    pub y_range: Range<isize>,
    pub z_range: Range<isize>,
}

impl<T> Cube<T> {
    pub fn new(values: Vec<Vec<Vec<T>>>, x_range: Range<isize>, y_range: Range<isize>, z_range: Range<isize>) -> Self {
        return Cube { values, x_range, y_range, z_range };
    }

    pub fn contains(&self, position: &Position) -> bool {
        return self.x_range.contains(&position.x) && self.y_range.contains(&position.y) && self.z_range.contains(&position.z);
    }
}

#[derive(Debug, Copy, Clone, Hash, Eq, PartialEq)]
pub struct Position {
    pub x: isize,
    pub y: isize,
    pub z: isize,
}

impl Position {
    pub fn new(x: isize, y: isize, z: isize) -> Self {
        Position { x, y, z }
    }
}

impl<T> Index<&Position> for Cube<T> {
    type Output = T;

    fn index(&self, position: &Position) -> &Self::Output {
        return &self.values[(position.x - self.x_range.start) as usize][(position.y - self.y_range.start) as usize][(position.z - self.z_range.start) as usize];
    }
}

impl<T> IndexMut<&Position> for Cube<T> {
    fn index_mut(&mut self, position: &Position) -> &mut Self::Output {
        return &mut self.values[(position.x - self.x_range.start) as usize][(position.y - self.y_range.start) as usize][(position.z - self.z_range.start) as usize];
    }
}


#[derive(Debug, Clone, Hash, Eq, PartialEq)]
pub struct Region {
    pub x_range: RangeInclusive<isize>,
    pub y_range: RangeInclusive<isize>,
    pub z_range: RangeInclusive<isize>,
}

impl Region {
    pub fn new(x_range: RangeInclusive<isize>, y_range: RangeInclusive<isize>, z_range: RangeInclusive<isize>) -> Self {
        return Region { x_range, y_range, z_range };
    }

    pub fn intersect(&self, other: &Region) -> bool {
        return range_intersect(&self.x_range, &other.x_range)
            && range_intersect(&self.y_range, &other.y_range)
            && range_intersect(&self.z_range, &other.z_range);
    }

    // TODO ??? optimize !!!
    pub fn carve(&self, other: &Region) -> Vec<Region> {
        let mut results = Vec::<Region>::new();
        let mut splits_x = VecDeque::<Region>::new();
        let mut splits_y = VecDeque::<Region>::new();
        let mut splits_z = VecDeque::<Region>::new();
        splits_x.push_back(other.clone());

        // println!("splits_x={:?} results={:?}", splits_x, results);

        while let Some(region) = splits_x.pop_front() {
            if !self.intersect(&region) {
                results.push(region);
                continue;
            }

            // X
            if region.x_range.contains(self.x_range.start()) && region.x_range.contains(self.x_range.end()) {
                // split x
                push_region(&mut splits_y, Region::new(*region.x_range.start()..=*self.x_range.start() - 1, region.y_range.clone(), region.z_range.clone()));
                push_region(&mut splits_y, Region::new(*self.x_range.start()..=*self.x_range.end(), region.y_range.clone(), region.z_range.clone()));
                push_region(&mut splits_y, Region::new(*self.x_range.end() + 1..=*region.x_range.end(), region.y_range.clone(), region.z_range.clone()));
            } else if region.x_range.contains(self.x_range.start()) {
                // split x
                push_region(&mut splits_y, Region::new(*region.x_range.start()..=*self.x_range.start() - 1, region.y_range.clone(), region.z_range.clone()));
                push_region(&mut splits_y, Region::new(*self.x_range.start()..=*region.x_range.end(), region.y_range.clone(), region.z_range.clone()));
            } else if region.x_range.contains(self.x_range.end()) {
                // split x
                push_region(&mut splits_y, Region::new(*region.x_range.start()..=*self.x_range.end(), region.y_range.clone(), region.z_range.clone()));
                push_region(&mut splits_y, Region::new(*self.x_range.end() + 1..=*region.x_range.end(), region.y_range.clone(), region.z_range.clone()));
            } else {
                push_region(&mut splits_y, region);
            }
        }

        // println!("splits_y={:?} results={:?}", splits_y, results);

        while let Some(region) = splits_y.pop_front() {
            if !self.intersect(&region) {
                results.push(region);
                continue;
            }

            // Y
            if region.y_range.contains(self.y_range.start()) & &region.y_range.contains(self.y_range.end()) {
                // split y
                push_region(&mut splits_z, Region::new(region.x_range.clone(), *region.y_range.start()..=*self.y_range.start() - 1, region.z_range.clone()));
                push_region(&mut splits_z, Region::new(region.x_range.clone(), *self.y_range.start()..=*self.y_range.end(), region.z_range.clone()));
                push_region(&mut splits_z, Region::new(region.x_range.clone(), *self.y_range.end() + 1..=*region.y_range.end(), region.z_range.clone()));
            } else if region.y_range.contains(self.y_range.start()) {
                // split y
                push_region(&mut splits_z, Region::new(region.x_range.clone(), *region.y_range.start()..=*self.y_range.start() - 1, region.z_range.clone()));
                push_region(&mut splits_z, Region::new(region.x_range.clone(), *self.y_range.start()..=*region.y_range.end(), region.z_range.clone()));
            } else if region.y_range.contains(self.y_range.end()) {
                // split y
                push_region(&mut splits_z, Region::new(region.x_range.clone(), *region.y_range.start()..=*self.y_range.end(), region.z_range.clone()));
                push_region(&mut splits_z, Region::new(region.x_range.clone(), *self.y_range.end() + 1..=*region.y_range.end(), region.z_range.clone()));
            } else {
                push_region(&mut splits_z, region);
            }
        }

        // println!("splits_z={:?} results={:?}", splits_z, results);

        while let Some(region) = splits_z.pop_front() {
            if !self.intersect(&region) {
                results.push(region);
                continue;
            }

            // Z
            if region.z_range.contains(self.z_range.start()) & &region.z_range.contains(self.z_range.end()) {
                // split z
                push_region(&mut splits_x, Region::new(region.x_range.clone(), region.y_range.clone(), *region.z_range.start()..=*self.z_range.start() - 1));
                push_region(&mut splits_x, Region::new(region.x_range.clone(), region.y_range.clone(), *self.z_range.start()..=*self.z_range.end()));
                push_region(&mut splits_x, Region::new(region.x_range.clone(), region.y_range.clone(), *self.z_range.end() + 1..=*region.z_range.end()));
            } else if region.z_range.contains(self.z_range.start()) {
                // split z
                push_region(&mut splits_x, Region::new(region.x_range.clone(), region.y_range.clone(), *region.z_range.start()..=*self.z_range.start() - 1));
                push_region(&mut splits_x, Region::new(region.x_range.clone(), region.y_range.clone(), *self.z_range.start()..=*region.z_range.end()));
            } else if region.z_range.contains(self.z_range.end()) {
                // split z
                push_region(&mut splits_x, Region::new(region.x_range.clone(), region.y_range.clone(), *region.z_range.start()..=*self.z_range.end()));
                push_region(&mut splits_x, Region::new(region.x_range.clone(), region.y_range.clone(), *self.z_range.end() + 1..=*region.z_range.end()));
            } else {
                push_region(&mut splits_x, region);
            }
        }

        // println!("splits_x={:?} results={:?}", splits_x, results);

        while let Some(region) = splits_x.pop_front() {
            if !self.intersect(&region) {
                results.push(region);
            }
        }

        // println!("results={:?}", results);

        return results;
    }
}

fn push_region(sink: &mut VecDeque<Region>, region: Region) {
    if !region.x_range.is_empty() && !region.y_range.is_empty() && !region.z_range.is_empty() {
        sink.push_back(region);
    }
}

fn range_intersect<T>(first: &RangeInclusive<T>, second: &RangeInclusive<T>) -> bool where T: PartialOrd<T> {
    return first.contains(second.start()) || first.contains(second.end()) || second.contains(first.start()) || second.contains(first.end());
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_disjoint() {
        let start = Region::new(-5..=-1, -5..=-1, -5..=-1);
        let other = Region::new(1..=5, 1..=5, 1..=5);
        let expected = vec![
            Region::new(1..=5, 1..=5, 1..=5)
        ];
        assert_eq!(start.carve(&other), expected)
    }

    #[test]
    fn test_corner1() {
        let start = Region::new(-5..=5, -5..=5, -5..=5);
        let other = Region::new(0..=10, 0..=10, 0..=10);
        let expected = vec![
            Region::new(6..=10, 0..=10, 0..=10),
            Region::new(0..=5, 6..=10, 0..=10),
            Region::new(0..=5, 0..=5, 6..=10),
        ];
        assert_eq!(start.carve(&other), expected)
    }

    #[test]
    fn test_corner2() {
        let start = Region::new(-5..=5, -5..=5, -5..=5);
        let other = Region::new(-10..=0, -10..=0, -10..=0);
        let expected = vec![
            Region::new(-10..=-6, -10..=0, -10..=0),
            Region::new(-5..=0, -10..=-6, -10..=0),
            Region::new(-5..=0, -5..=0, -10..=-6),
        ];
        assert_eq!(start.carve(&other), expected)
    }

    #[test]
    fn test_slice_x() {
        let start = Region::new(-5..=5, -50..=50, -50..=50);
        let other = Region::new(-10..=10, -10..=10, -10..=10);
        let expected = vec![
            Region::new(-10..=-6, -10..=10, -10..=10),
            Region::new(6..=10, -10..=10, -10..=10),
        ];
        assert_eq!(start.carve(&other), expected)
    }

    #[test]
    fn test_slice_y() {
        let start = Region::new(-50..=50, -5..=5, -50..=50);
        let other = Region::new(-10..=10, -10..=10, -10..=10);
        let expected = vec![
            Region::new(-10..=10, -10..=-6, -10..=10),
            Region::new(-10..=10, 6..=10, -10..=10),
        ];
        assert_eq!(start.carve(&other), expected)
    }

    #[test]
    fn test_slice_z() {
        let start = Region::new(-50..=50, -50..=50, -5..=5);
        let other = Region::new(-10..=10, -10..=10, -10..=10);
        let expected = vec![
            Region::new(-10..=10, -10..=10, -10..=-6),
            Region::new(-10..=10, -10..=10, 6..=10),
        ];
        assert_eq!(start.carve(&other), expected)
    }

    #[test]
    fn test_contained() {
        let start = Region::new(-5..=5, -5..=5, -5..=5);
        let other = Region::new(-10..=10, -10..=10, -10..=10);
        let expected = vec![
            Region::new(-10..=-6, -10..=10, -10..=10),
            Region::new(6..=10, -10..=10, -10..=10),
            Region::new(-5..=5, -10..=-6, -10..=10),
            Region::new(-5..=5, 6..=10, -10..=10),
            Region::new(-5..=5, -5..=5, -10..=-6),
            Region::new(-5..=5, -5..=5, 6..=10),
        ];
        assert_eq!(start.carve(&other), expected)
    }
}
