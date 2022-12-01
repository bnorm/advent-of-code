pub fn part1() -> String {
    let input = include_str!("res/input01.txt").lines().collect::<Vec<_>>();
    let mut elves = parse_sections::<i64>(&input)
        .map(|elf| { elf.sum::<i64>() })
        .collect::<Vec<_>>();
    elves.sort_unstable();
    elves.reverse();

    let snacks = elves[0];
    return format!("{}", snacks);
}

pub fn part2() -> String {
    let input = include_str!("res/input01.txt").lines().collect::<Vec<_>>();
    let mut elves = parse_sections::<i64>(&input)
        .map(|elf| { elf.sum::<i64>() })
        .collect::<Vec<_>>();
    elves.sort_unstable();
    elves.reverse();

    let total = elves[0] + elves[1] + elves[2];
    return format!("{}", total);
}

fn parse_sections<'a, F>(
    lines: &'a Vec<&str>
) -> impl Iterator<Item=impl Iterator<Item=F> + 'a> + 'a
    where F: std::str::FromStr,
          F::Err: std::fmt::Debug
{
    return lines.split(|line| line.len() == 0)
        .map(|section| {
            section.iter()
                .map(|line| { line.parse::<F>().unwrap() })
        });
}

