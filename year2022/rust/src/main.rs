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
            _ => None,
        };
        println!(
            "[{}] result: {}",
            part,
            result.unwrap_or(String::from("UNKNOWN"))
        );
    }
}
