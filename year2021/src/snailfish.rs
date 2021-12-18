use std::{fmt, io, str};
use std::fmt::{Debug, Display, Formatter};
use std::io::ErrorKind;
use std::str::{Chars, FromStr};

#[derive(PartialEq, PartialOrd, Eq, Ord, Hash, Clone)]
pub enum Node {
    Value(u32),
    Pair(Box<Node>, Box<Node>),
}

impl Display for Node {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        fn recurse(f: &mut Formatter<'_>, node: &Node) -> fmt::Result {
            match node {
                Node::Value(v) => {
                    write!(f, "{}", v)?;
                }
                Node::Pair(left, right) => {
                    write!(f, "[")?;
                    recurse(f, left)?;
                    write!(f, ",")?;
                    recurse(f, right)?;
                    write!(f, "]")?;
                }
            }
            fmt::Result::Ok(())
        }

        recurse(f, self)
    }
}

impl Debug for Node {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result { write!(f, "{}", self) }
}

impl FromStr for Node {
    type Err = io::Error; // TODO custom error

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        fn recurse(chars: &mut Chars) -> Result<Node, io::Error> {
            return match chars.next() {
                Some('[') => {
                    let left = recurse(chars)?;
                    if let Some(',') = chars.next() {} else {
                        return Err(io::Error::new(ErrorKind::InvalidInput, "expected ','"));
                    }
                    let right = recurse(chars)?;
                    if let Some(']') = chars.next() {} else {
                        return Err(io::Error::new(ErrorKind::InvalidInput, "expected ']'"));
                    }
                    Ok(Node::pair(left, right))
                }
                Some(c) => {
                    let mut number = String::new();
                    number.push(c);
                    let mut peek = chars.clone();
                    while let Some(c) = peek.next() {
                        if c >= '0' && c <= '9' {
                            number.push(chars.next().unwrap());
                        } else {
                            break;
                        }
                    }
                    // TODO convert error
                    Ok(Node::Value(number.parse::<u32>().expect("not a number")))
                }
                None => Err(io::Error::from(ErrorKind::UnexpectedEof))
            };
        }

        return recurse(&mut s.trim().chars());
    }
}

impl Node {
    pub fn pair(left: Node, right: Node) -> Node {
        Node::Pair(Box::from(left), Box::from(right))
    }

    pub fn value(value: u32) -> Node {
        Node::Value(value)
    }

    pub fn magnitude(&self) -> u128 {
        match self {
            Node::Value(n) => *n as u128,
            Node::Pair(left, right) => 3 * left.magnitude() + 2 * right.magnitude(),
        }
    }

    pub fn add_nodes(nodes: &Vec<Node>) -> Option<Node> {
        let mut iter = nodes.iter();
        let mut node = iter.next()?.clone();
        while let Some(next) = iter.next() {
            node = node.add(next);
        }
        return Some(node);
    }

    pub fn add(&self, right: &Node) -> Node {
        let mut node = Node::pair(self.clone(), right.clone());
        // println!("after addition: {}", node);
        loop {
            if let Some(n) = node.explode() {
                node = n;
                // println!("after explode:  {}", node);
            } else if let Some(n) = node.split() {
                node = n;
                // println!("after split:    {}", node);
            } else {
                break;
            }
        }
        return node;
    }

    fn explode(&self) -> Option<Node> {
        fn add_left(node: &Node, addition: u32) -> Node {
            match node {
                Node::Value(v) => Node::Value(*v + addition),
                Node::Pair(left, right) => {
                    Node::pair(add_left(left, addition), *right.clone())
                }
            }
        }

        fn add_right(node: &Node, addition: u32) -> Node {
            match node {
                Node::Value(v) => Node::Value(*v + addition),
                Node::Pair(left, right) => {
                    Node::pair(*left.clone(), add_right(right, addition))
                }
            }
        }

        fn recurse(node: &Node, depth: usize) -> Option<(Node, Option<u32>, Option<u32>)> {
            return match node {
                Node::Value(_) => None,
                Node::Pair(left, right) => {
                    if depth >= 4 {
                        let left_value = if let Node::Value(v) = **left { v } else { unreachable!() };
                        let right_value = if let Node::Value(v) = **right { v } else { unreachable!() };
                        Some((Node::Value(0), Some(left_value), Some(right_value)))
                    } else {
                        if let Some((node, l, r)) = recurse(left, depth + 1) {
                            let new_right = if let Some(a) = r { add_left(right, a) } else { *right.clone() };
                            Some((Node::pair(node, new_right), l, None))
                        } else if let Some((node, l, r)) = recurse(right, depth + 1) {
                            let new_left = if let Some(a) = l { add_right(left, a) } else { *left.clone() };
                            Some((Node::pair(new_left, node), None, r))
                        } else {
                            None
                        }
                    }
                }
            };
        }

        return if let Some((node, _, _)) = recurse(self, 0) { Some(node) } else { None };
    }

    fn split(&self) -> Option<Node> {
        return match self {
            Node::Value(v) => {
                if *v >= 10 {
                    Some(Node::pair(Node::Value(*v / 2), Node::Value(*v / 2 + *v % 2)))
                } else {
                    None
                }
            }
            Node::Pair(left, right) => {
                if let Some(node) = left.split() {
                    Some(Node::pair(node, *right.clone()))
                } else if let Some(node) = right.split() {
                    Some(Node::pair(*left.clone(), node))
                } else {
                    None
                }
            }
        };
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_parse() {
        let actual = "[[8,10],[9,7]]".parse::<Node>().unwrap();
        let expected = Node::pair(
            Node::pair(
                Node::Value(8),
                Node::Value(10),
            ),
            Node::pair(
                Node::Value(9),
                Node::Value(7),
            ),
        );
        assert_eq!(expected, actual);
    }

    #[test]
    fn test_magnitude() -> Result<(), io::Error> {
        assert_eq!(29, "[9,1]".parse::<Node>()?.magnitude());
        assert_eq!(21, "[1,9]".parse::<Node>()?.magnitude());
        assert_eq!(129, "[[9,1],[1,9]]".parse::<Node>()?.magnitude());
        Ok(())
    }

    #[test]
    fn test_split() -> Result<(), io::Error> {
        assert_eq!(
            "[15,13]".parse::<Node>()?.split(),
            Some("[[7,8],13]".parse::<Node>()?),
        );
        assert_eq!(
            "[[[[0,7],4],[15,[0,13]]],[1,1]]".parse::<Node>()?.split(),
            Some("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]".parse::<Node>()?),
        );
        assert_eq!(
            "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]".parse::<Node>()?.split(),
            Some("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]".parse::<Node>()?),
        );
        Ok(())
    }

    #[test]
    fn test_explode() -> Result<(), io::Error> {
        assert_eq!(
            Some("[[[[0,9],2],3],4]".parse::<Node>()?),
            "[[[[[9,8],1],2],3],4]".parse::<Node>()?.explode(),
        );
        assert_eq!(
            Some("[7,[6,[5,[7,0]]]]".parse::<Node>()?),
            "[7,[6,[5,[4,[3,2]]]]]".parse::<Node>()?.explode(),
        );
        assert_eq!(
            Some("[[6,[5,[7,0]]],3]".parse::<Node>()?),
            "[[6,[5,[4,[3,2]]]],1]".parse::<Node>()?.explode(),
        );
        assert_eq!(
            Some("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]".parse::<Node>()?),
            "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]".parse::<Node>()?.explode(),
        );
        assert_eq!(
            Some("[[3,[2,[8,0]]],[9,[5,[7,0]]]]".parse::<Node>()?),
            "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]".parse::<Node>()?.explode(),
        );
        Ok(())
    }

    #[test]
    fn test_add() -> Result<(), io::Error> {
        assert_eq!(
            Node::add_nodes(&vec![
                "[1,1]".parse::<Node>()?,
                "[2,2]".parse::<Node>()?,
                "[3,3]".parse::<Node>()?,
                "[4,4]".parse::<Node>()?,
            ]),
            Some("[[[[1,1],[2,2]],[3,3]],[4,4]]".parse::<Node>()?),
        );
        assert_eq!(
            Node::add_nodes(&vec![
                "[1,1]".parse::<Node>()?,
                "[2,2]".parse::<Node>()?,
                "[3,3]".parse::<Node>()?,
                "[4,4]".parse::<Node>()?,
                "[5,5]".parse::<Node>()?,
            ]),
            Some("[[[[3,0],[5,3]],[4,4]],[5,5]]".parse::<Node>()?),
        );
        assert_eq!(
            Node::add_nodes(&vec![
                "[1,1]".parse::<Node>()?,
                "[2,2]".parse::<Node>()?,
                "[3,3]".parse::<Node>()?,
                "[4,4]".parse::<Node>()?,
                "[5,5]".parse::<Node>()?,
                "[6,6]".parse::<Node>()?,
            ]),
            Some("[[[[5,0],[7,4]],[5,5]],[6,6]]".parse::<Node>()?),
        );
        assert_eq!(
            Node::add_nodes(&vec![
                "[[[[4,3],4],4],[7,[[8,4],9]]]".parse::<Node>()?,
                "[1,1]".parse::<Node>()?,
            ]),
            Some("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]".parse::<Node>()?),
        );
        assert_eq!(
            Node::add_nodes(&vec![
                "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]".parse::<Node>()?,
                "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]".parse::<Node>()?,
                "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]".parse::<Node>()?,
                "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]".parse::<Node>()?,
                "[7,[5,[[3,8],[1,4]]]]".parse::<Node>()?,
                "[[2,[2,2]],[8,[8,1]]]".parse::<Node>()?,
                "[2,9]".parse::<Node>()?,
                "[1,[[[9,3],9],[[9,0],[0,7]]]]".parse::<Node>()?,
                "[[[5,[7,4]],7],1]".parse::<Node>()?,
                "[[[[4,2],2],6],[8,7]]".parse::<Node>()?,
            ]),
            Some("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]".parse::<Node>()?),
        );
        Ok(())
    }

    #[test]
    fn test_homework() -> Result<(), io::Error> {
        assert_eq!(
            Node::add_nodes(&vec![
                "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]".parse::<Node>()?,
                "[[[5,[2,8]],4],[5,[[9,9],0]]]".parse::<Node>()?,
                "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]".parse::<Node>()?,
                "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]".parse::<Node>()?,
                "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]".parse::<Node>()?,
                "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]".parse::<Node>()?,
                "[[[[5,4],[7,7]],8],[[8,3],8]]".parse::<Node>()?,
                "[[9,3],[[9,9],[6,[4,9]]]]".parse::<Node>()?,
                "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]".parse::<Node>()?,
                "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]".parse::<Node>()?,
            ]).unwrap().magnitude(),
            4140,
        );
        Ok(())
    }
}
