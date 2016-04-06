function demo()

clear;
clc;

%please global these variables
global k l M step window itrCounter dynamic

dynamic = 1;
step = 10;
window = 200;
itrCounter = 1;
M = 2;
k = 20;
l = 4;

mop = testmop('ud1',11);

end
