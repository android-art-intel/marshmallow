
out: dump_parser
	./dump_parser < examples/dump_simple > out

dump_parser: lex.yy.c opt.tab.cpp key_values.h instruction.h helpers.h dumper_handler.h
	g++ -o $@ lex.yy.c opt.tab.cpp -std=gnu++0x  -ly -lfl

lex.yy.c: opt.flex opt.tab.hpp
	flex opt.flex

opt.tab.hpp: opt.ypp
	bison --defines $<

opt.tab.cpp: opt.ypp
	bison --defines $<

clean:
	rm -f dump_parser left lex.yy.c opt.tab.hpp opt.tab.cpp out tmp opt.output *dot

