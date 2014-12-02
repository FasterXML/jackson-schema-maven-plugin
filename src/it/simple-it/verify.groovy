File touchFile1 = new File(basedir, "target/schema.json")
assert touchFile1.isFile()
assert touchFile1.text.contains("Nested2")

File touchFile2 = new File(basedir, "target/output2.json")
assert touchFile2.isFile()

assert !touchFile2.text.contains("Nested2")
