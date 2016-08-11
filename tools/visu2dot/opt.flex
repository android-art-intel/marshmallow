%{
#include <math.h>
#include "opt.tab.hpp"
#include "dumper_handler.h"
%}

DIGIT    [0-9]
ID       [a-z_A-Z][A-Za-z0-9_]*


%%

begin_compilation {
  DEBUG_PRINT("Begin comp%s\n", yytext);
  return BEGIN_COMP;
}

end_compilation {
  DEBUG_PRINT("comp%s\n", yytext);
  return END_COMP;
}

begin_cfg {
  DEBUG_PRINT("beg cfg %s\n", yytext);
  return BEGIN_CFG;
}

begin_block {
  DEBUG_PRINT("beg block %s\n", yytext);
  return BEGIN_BLOCK;
}

begin_states {
  DEBUG_PRINT("beg states %s\n", yytext);
  return BEGIN_STATE;
}

begin_locals {
  DEBUG_PRINT("beg locals %s\n", yytext);
  return BEGIN_LOCALS;
}

begin_HIR {
  DEBUG_PRINT("beg hir %s\n", yytext);
  return BEGIN_HIR;
}

end_cfg {
  DEBUG_PRINT("cfg %s\n", yytext);
  return END_CFG;
}

end_block {
  DEBUG_PRINT("block  %s\n", yytext);
  return END_BLOCK;
}

end_states {
  DEBUG_PRINT("states %s\n", yytext);
  return END_STATE;
}

end_locals {
  DEBUG_PRINT("locals %s\n", yytext);
  return END_LOCALS;
}

end_HIR {
  DEBUG_PRINT("hir %s\n", yytext);
  return END_HIR;
}

[-+]{0,1}{DIGIT}+ {
  DEBUG_PRINT("Integer %s\n", yytext);
  yylval.l = strtoll(yytext, nullptr, 0);
  return INTEGER;
}

\"[^\"]*\" {
  DEBUG_PRINT("String %s\n", yytext);
  int len = strlen(yytext);
  yytext[len - 1] = '\0';
  yylval.string = strdup(yytext + 1);
  return STRING;
}

[-+]{0,1}{DIGIT}*"."{DIGIT}*        {
  DEBUG_PRINT("Float %s\n", yytext);
  yylval.d = strtod(yytext, nullptr);
  return FLOAT;
}

{ID} {
  DEBUG_PRINT("ID %s\n", yytext);
  yylval.string = strdup(yytext);
  return IDENTIFIER;
}

\n {
  DEBUG_PRINT("Have a EOL\n");
  return EOL;
}

\(.*\) {
  // Skip options
  DEBUG_PRINT("Skipping %s\n", yytext);
  return OPTIONS;
}

" " {
}

. {
  DEBUG_PRINT("Stray character %s\n", yytext);
  return yytext[0];
}
