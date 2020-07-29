import sys
def main(path):
    try:
        with open(path, "r") as logFile:
            cnt = 0
            TJ_total, TS_total = 0.0, 0.0
            while True:
                line = logFile.readline()
                if not line:
                    break
                
                TJ, TS = line.split(", ")
                TJ = TJ.split("=")[1]
                TS = TS.split("=")[1]
                if TJ == '0.00' or TS == '0.00':
                    print(line)
                    continue
                cnt += 1
                TJ_total += float(TJ)
                TS_total += float(TS)
            print("TS = %.2f, TJ = %.2f" % (TS_total / cnt, TJ_total / cnt))
    except:
        print("File Error.")


if __name__ == '__main__':
    main(sys.argv[1])
    


