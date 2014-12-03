File touchFile1 = new File(basedir, "target/schema.json")
assert touchFile1.isFile()
assert touchFile1.text.contains("any")
