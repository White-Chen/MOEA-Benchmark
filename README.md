# MOEA-Benchmark
MOEA-Benchmark is a benchmark utils for test Multi-Objective Optimization Alogrithm in Matlab
## list of Benchmark
Stationary Multi-Objective Optimization Problems(Include Many-Objective Optimization Problems)   
   

*   ZDT Benchmark - ZDT1, ZDT2, ZDT3, ZDT5, ZDT6
*   DTLZ Benchmark - DTLZ1, DTLZ2, DTLZ3, DTLZ4, DTLZ5, DTLZ6, DTLZ7
*   WFG Benchmark - WFG1, WGF2, WFG3, WFG4, WFG5, WFG6, WFG7, WFG8, WFG9, WFG10
*   CEC09 Benchmark without constraints(UF Benchmark) - UF1, UF2, UF3, UF4, UF5, UF6, UF7, UF8, UF9, UF10
*   CEC09 Benchmark with constraints(CF Benchmark) - CF1, CF2, CF3, CF4, CF5, CF6, CF7, CF8, CF9 ,CF10   
   

Dynamic Multi-Objective Optimzation Problems
*   FDA Benchmark - FDA1, FDA2, FDA3, FDA4, FDA5
*   CEC2014 Benchmark without constraints(UD) - UD1, UD2, UD3, UD4, UD5, UD6, UD7, UD8, UD9, UD10, UD11, UD12, UD13, UD14
*   CEC2015 Benchmark with constraints(CD) - CD1, CD2

## list of Ref.
1. Huband S, Hingston P, Barone L, While L, 2006, A review of multiobjective test problems and a scalable test problem toolkit. IEEE Transactions on Evolutionary Computation, 10(5), pp477-506.
2. Zitzler, E., Deb, K., & Thiele, L. (2000). Comparison of multiobjective evolutionary algorithms: Empirical results. Evolutionary computation, 8(2), 173-195. 3. Deb, K., Thiele, L., Laumanns, M., & Zitzler, E. (2005). Scalable test problems for evolutionary multiobjective optimization (pp. 105-145). Springer London.
4. Zhang, Q., Zhou, A., Zhao, S., Suganthan, P. N., Liu, W., & Tiwari, S. (2008). Multiobjective optimization test instances for the CEC 2009 special session and competition. University of Essex, Colchester, UK and Nanyang technological University, Singapore, special session on performance assessment of multi-objective optimization algorithms, technical report, 264. 5. Farina, M., Deb, K., & Amato, P. (2004). Dynamic multiobjective optimization problems: test cases, approximations, and applications. Evolutionary Computation, IEEE Transactions on, 8(5), 425-442.
6. Biswas, S., Das, S., Suganthan, P., & Coello Coello, C. (2014, July). Evolutionary multiobjective optimization in dynamic environments: A set of novel benchmark functions. In Evolutionary Computation (CEC), 2014 IEEE Congress on (pp. 3192-3199). IEEE.

## DEMO
*   If use DTLZ function   
<pre><code>	
	global M k                         % global variables M k
	M = 3;                             % M is the number of Objective demension
	k = 5                              % k is control params to control the number of demension, dimension = M + k -1
	mop = benchmark('DTLZ1',7);        % for dtlz1 k = 1
	% mop = benchmark('DTLZ2',12);     % for dtlz1-6 k = 10
	% mop = benchmark('DTLZ7',22);     % for dtlz7 = 20
	results = mop.func(population)     % population is the evolutionary pop, results is a matrix, the column is popsize, row is objetive value
</code></pre>
*   If use WFG function   
<pre><code>
	global M k l                     % global variables M k l
	M = 2;
	k = 2;
	l = 4;                           % k and l are both control parms, dimension = k + l
        mop = benchmark('wfg11111111111',6)		 % for wfg1 k = 2 and l = 4, dim = 4 + 2 = 6
	results = mop.func(population)   % like above
</code></pre>
*   If use dynamic problem   
<pre><code>
	global step window itrCounter    % global variables step window and iteration counter
	step = 10                        % number of dynamic steps
	window = 200                     % number of stationary iterations between two dynamic
	itrCounter                       % init iteration counter, in algorithm loop this should be +1
	mop = benchmark('fda1',30)       % this decision number can be modify by the second input
	results = mop.func(population)   % like above
</code></pre>
