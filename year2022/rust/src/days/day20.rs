pub fn part1() -> String {
    let values = include_str!("res/input20.txt").lines()
        .enumerate()
        .map(|(index, line)| Input { original_index: index, value: line.parse::<isize>().unwrap() })
        .collect::<Vec<_>>();

    let mixed = mixing(&values, 1);
    let th0 = mixed.iter().position(|v| v.value == 0).unwrap();

    let th1000 = mixed[(th0 + 1000) % mixed.len()].value;
    let th2000 = mixed[(th0 + 2000) % mixed.len()].value;
    let th3000 = mixed[(th0 + 3000) % mixed.len()].value;
    return format!("{:?}", th1000 + th2000 + th3000);
}

pub fn part2() -> String {
    let values = include_str!("res/input20.txt").lines()
        .enumerate()
        .map(|(index, line)| Input { original_index: index, value: line.parse::<isize>().unwrap() * 811_589_153 })
        .collect::<Vec<_>>();

    let mixed = mixing(&values, 10);
    let th0 = mixed.iter().position(|v| v.value == 0).unwrap();

    let th1000 = mixed[(th0 + 1000) % mixed.len()].value;
    let th2000 = mixed[(th0 + 2000) % mixed.len()].value;
    let th3000 = mixed[(th0 + 3000) % mixed.len()].value;
    return format!("{:?}", th1000 + th2000 + th3000);
}

fn mixing(values: &Vec<Input>, count: usize) -> Vec<Input> {
    let mut mixed = values.clone();
    let size_m1 = mixed.len() as isize - 1;

    for _ in 0..count {
        for n in values {
            if n.value == 0 { continue }

            let source_index = mixed.iter().position(|v| v.original_index == n.original_index).unwrap();
            let moved = mixed.remove(source_index);

            let mut destination_index = source_index as isize + n.value;
            destination_index = destination_index % size_m1;
            if destination_index < 0 {
                destination_index += size_m1;
            }
            if destination_index == 0 {
                mixed.push(moved);
            } else {
                mixed.insert(destination_index as usize, moved);
            }
        }
    }

    return mixed;
}

// There are duplicates in the input so need to match based on the original
// index rather than value.
#[derive(Debug, Clone)]
struct Input {
    pub original_index: usize,
    pub value: isize,
}
