use std::collections::HashSet;

pub fn part1() -> String {
    let input = include_str!("res/input03.txt").lines().collect::<Vec<_>>();

    let items = input.iter()
        .filter_map(|pack| {
            let first: HashSet<char> = pack[0..pack.len() / 2].chars().collect();
            let second: HashSet<char> = pack[pack.len() / 2..pack.len()].chars().collect();
            first.intersection(&second).next().copied()
        })
        .map(|item| { priority(&item) })
        .sum::<i32>();

    return format!("{:?}", items);
}

pub fn part2() -> String {
    let input = include_str!("res/input03.txt").lines().collect::<Vec<_>>();

    let items = input
        .chunks(3)
        .filter_map(|chunk| {
            let first: HashSet<char> = chunk[0].chars().collect();
            let second: HashSet<char> = chunk[1].chars().collect();
            let third: HashSet<char> = chunk[2].chars().collect();

            let intersection1 = first.intersection(&second).map(|c| { *c }).collect::<HashSet<char>>();
            third.intersection(&intersection1)
                .next().copied()
        })
        .map(|item| { priority(&item) })
        .sum::<i32>();

    return format!("{:?}", items);
}

fn priority(item: &char) -> i32 {
    if item >= &'a' && item <= &'z' {
        1 + (*item as i32 - 'a' as i32)
    } else {
        27 + (*item as i32 - 'A' as i32)
    }
}
