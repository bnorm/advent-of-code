use clap::{App, ErrorKind, IntoApp, Parser};

use days::*;

mod days;

#[derive(Parser)]
#[clap(about, version, author)]
struct Cli {
    #[clap()]
    parts: Vec<String>,
}

fn main() {
    let args: Cli = Cli::parse();

    if args.parts.is_empty() {
        let mut app: App = Cli::into_app();
        app.error(
            ErrorKind::MissingRequiredArgument,
            "Most provide at least one part",
        )
        .exit();
    }
    for part in args.parts {
        let result = match part.as_ref() {
            "1a" => Some(day01::part1()),
            "1b" => Some(day01::part2()),
            "2a" => Some(day02::part1()),
            "2b" => Some(day02::part2()),
            "3a" => Some(day03::part1()),
            "3b" => Some(day03::part2()),
            "4a" => Some(day04::part1()),
            "4b" => Some(day04::part2()),
            "5a" => Some(day05::part1()),
            "5b" => Some(day05::part2()),
            "6a" => Some(day06::part1()),
            "6b" => Some(day06::part2()),
            "7a" => Some(day07::part1()),
            "7b" => Some(day07::part2()),
            "8a" => Some(day08::part1()),
            "8b" => Some(day08::part2()),
            "9a" => Some(day09::part1()),
            "9b" => Some(day09::part2()),
            "10a" => Some(day10::part1()),
            "10b" => Some(day10::part2()),
            "11a" => Some(day11::part1()),
            "11b" => Some(day11::part2()),
            "12a" => Some(day12::part1()),
            "12b" => Some(day12::part2()),
            "13a" => Some(day13::part1()),
            "13b" => Some(day13::part2()),
            "14a" => Some(day14::part1()),
            "14b" => Some(day14::part2()),
            "15a" => Some(day15::part1()),
            "15b" => Some(day15::part2()),
            "16a" => Some(day16::part1()),
            "16b" => Some(day16::part2()),
            "17a" => Some(day17::part1()),
            "17b" => Some(day17::part2()),
            "18a" => Some(day18::part1()),
            "18b" => Some(day18::part2()),
            "19a" => Some(day19::part1()),
            "19b" => Some(day19::part2()),
            "20a" => Some(day20::part1()),
            "20b" => Some(day20::part2()),
            "21a" => Some(day21::part1()),
            "21b" => Some(day21::part2()),
            "22a" => Some(day22::part1()),
            "22b" => Some(day22::part2()),
            "23a" => Some(day23::part1()),
            "23b" => Some(day23::part2()),
            "24a" => Some(day24::part1()),
            "24b" => Some(day24::part2()),
            "25a" => Some(day25::part1()),
            "25b" => Some(day25::part2()),
            _ => None,
        };
        println!(
            "[{}] result: {}",
            part,
            result.unwrap_or(String::from("UNKNOWN"))
        );
    }
}
