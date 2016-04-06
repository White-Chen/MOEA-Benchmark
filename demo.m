function demo()
clear;
clc;
global k l M step window itrCounter dynamic;
dynamic = 1;
step = 10;
window = 200;
itrCounter = 1;
M = 2;
k = 20;
l = 4;
mop = testmop('ud1',11);
subproblems = moead( mop, 'popsize', 100, 'niche', 20, 'iteration', 2000, 'method', 'te');
%pareto = moead( mop, 'popsize', 100, 'niche', 20, 'iteration', 200, 'method', 'ws');

end