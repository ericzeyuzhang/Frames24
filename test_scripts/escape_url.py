import urllib.parse
queryFile = open("./query_load.txt", "r")
queryFileEscape = open("./query_load_escape.txt", "w")
cnt = 0
while True:
    line = queryFile.readline()
    if not line:
        break
    cnt += 1
    # print(line)
        # line_escape = urllib.parse.quote(line)
        # print(line_escape)
    queryFileEscape.write(urllib.parse.quote(line[:-1]) + '\n')
print(cnt)
    