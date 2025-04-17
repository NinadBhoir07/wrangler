# Parse ByteSize

The PARSE-BYTESIZE directive parses human-readable byte size strings into their equivalent numeric byte values.

## Syntax

parse-bytesize :<column>

## Usage Notes

The PARSE-BYTESIZE directive will parse a column representing byte sizes like `5KB`, `1.5MB`, `2G`, etc., into their numeric values in **bytes**.

The column should be of type string. If the column is `null` or the value is not a valid byte size format, this directive will throw an error. Supported units include `B`, `KB`, `MB`, `GB`, `TB`, and `PB`. The unit matching is **case-insensitive**, and decimals are supported.

If the column is already a numeric value, applying this directive is a no-op.

## Examples

If the column has value `"5KB"` or `"5kb"`, then after applying this directive, the column value becomes `5120`.

Other examples:

| Input Column | Parsed Output |
| ------------ | ------------- |
| `"1.5MB"`    | `1572864`     |
| `"2G"`       | `2147483648`  |
| `"500B"`     | `500`         |
