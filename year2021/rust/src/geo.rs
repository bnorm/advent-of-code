use std::cmp::{max, min};
use std::ops::Range;
use std::str::FromStr;
use serde::Deserialize;
use recap::Recap;

#[derive(Debug, PartialEq, Eq, Hash)]
pub struct Point {
    pub x: i32,
    pub y: i32,
}

#[derive(Debug, PartialEq, Eq, Hash)]
pub struct Line {
    pub p1: Point,
    pub p2: Point,
}

impl Line {
    fn x_range(&self) -> Range<i32> { min(self.p1.x, self.p2.x)..max(self.p1.x, self.p2.x) + 1 }
    fn y_range(&self) -> Range<i32> { min(self.p1.y, self.p2.y)..max(self.p1.y, self.p2.y) + 1 }

    fn mxb(&self) -> (i32, i32) {
        let m = (self.p2.y - self.p1.y) / (self.p2.x - self.p1.x);
        let b = self.p1.y - m * self.p1.x;
        return (m, b);
    }
}

pub fn intersection(l1: &Line, l2: &Line) -> Vec<Point> {
    let l1_vertical = l1.p1.x == l1.p2.x;
    let l2_vertical = l2.p1.x == l2.p2.x;

    let result = if l1_vertical && l2_vertical {
        if l1.p1.x == l2.p1.x {
            intersection_range(&l1.y_range(), &l2.y_range()).map(|y| Point { x: l1.p1.x, y }).collect()
        } else {
            vec!()
        }
    } else if l1_vertical {
        if l2.x_range().contains(&l1.p1.x) {
            let (m, b) = l2.mxb();
            let y = m * l1.p1.x + b;
            if l1.y_range().contains(&y) {
                vec!(Point { x: l1.p1.x, y })
            } else {
                vec!()
            }
        } else {
            vec!()
        }
    } else if l2_vertical {
        if l1.x_range().contains(&l2.p1.x) {
            let (m, b) = l1.mxb();
            let y = m * l2.p1.x + b;
            if l2.y_range().contains(&y) {
                vec!(Point { x: l2.p1.x, y })
            } else {
                vec!()
            }
        } else {
            vec!()
        }
    } else {
        let (m1, b1) = l1.mxb();
        let (m2, b2) = l2.mxb();

        if m2 != m1 {
            // for lines to intersect there must be a x where
            // m1 * x + b1 == m2 * x + b2
            // x = (b1 - b2) / (m2 - m1)

            let xf = (b1 - b2) as f32 / (m2 - m1) as f32;
            let x = xf as i32;
            // !!! Sometimes lines CAN intersect but not on an integer 'x' value which does not count !!!
            if xf.fract() == 0.0 && l1.x_range().contains(&x) && l2.x_range().contains(&x) {
                vec!(Point { x, y: m1 * x + b1 })
            } else {
                vec!()
            }
        } else {
            // parallel
            if b1 == b2 {
                intersection_range(&l1.x_range(), &l2.x_range()).map(|x| Point { x, y: m1 * x + b1 }).collect()
            } else {
                vec!()
            }
        }
    };

    return result;
}

fn intersection_range(a: &Range<i32>, b: &Range<i32>) -> Range<i32> {
    if b.start <= a.end && a.start <= b.end {
        max(a.start, b.start)..min(a.end, b.end)
    } else {
        1..0
    }
}

#[derive(Debug, Deserialize, Recap)]
#[recap(regex = r#"(?P<x1>\d+),(?P<y1>\d+) -> (?P<x2>\d+),(?P<y2>\d+)"#)]
struct LineRecap {
    pub x1: i32,
    pub y1: i32,
    pub x2: i32,
    pub y2: i32,
}

impl FromStr for Line {
    type Err = recap::Error;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        return s.parse::<LineRecap>().map(|l| {
            Line {
                p1: Point {
                    x: l.x1,
                    y: l.y1,
                },
                p2: Point {
                    x: l.x2,
                    y: l.y2,
                },
            }
        });
    }
}
