# Parse TimeDuration

The PARSE-TIMEDURATION directive parses human-readable time duration strings into their equivalent values in milliseconds.

## Syntax

parse-timeduration :<column>

## Usage Notes

The PARSE-TIMEDURATION directive converts durations like `5min`, `2h`, `1d` into their numeric value in **milliseconds**.

The column should be of type string. If the column is `null` or not a valid duration, the directive will throw an error. Supported units include: `ms`, `s`, `min`, `h`, and `d`. The matching is **case-insensitive**.

If the column is already a numeric value (in ms), applying this directive is a no-op.

## Examples

If the column has value `"5min"` or `"5MIN"`, then after applying this directive, the column value becomes `300000`.

Other examples:

| Input Column | Parsed Output |
| ------------ | ------------- |
| `"2h"`       | `7200000`     |
| `"1d"`       | `86400000`    |
| `"500ms"`    | `500`         |
