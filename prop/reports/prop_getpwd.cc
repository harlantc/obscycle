#include <stdlib.h>
#include <string.h>
#include <string>
#include <ObsUtil.hh>
#include <sys/types.h>
#include <sys/stat.h>



int main (int argc ,char **argv)
{
string pwd;
string pfile; 
char *env;


if  (argc < 2 || strstr(argv[1],"too") )  {
  env = getenv("USER");
  pfile = "/tmp/.";
  if (env) {
    pfile +=  env;
  }
  if  (argc == 2 && strstr(argv[1],"too") ) 
    pfile += ".pipi2"; 
  else
    pfile += ".pipi"; 
}
else {
  pfile = argv[1];
}

get_password(pwd);

write_password((char *)pwd.c_str(),(char *)pfile.c_str());
chmod(pfile.c_str(),0400);

}
