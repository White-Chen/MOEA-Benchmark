% Create 5/3
% Author ChenZhe

% FUN:				Objective value
% VAR:				Decision Value
% inProcessData: 	Process Data Acquisition
% IndicatorData: 	Indicator Data Acquisition
% 
% variable algorithm struct tree:
% algorithm-+-------------------------------------------------------------------------------------------------
% 			|   name:				[numOfAlgorithmsx1 char]
% 			|   date: 				'xx:xx:xx'
% 			|   bytes:				0
% 			|   isdir:				1
% 			|   datenum:			xx
% 			|   Path:				'..\Experiment Data\xx\data\xx'
% 			|   ProblemNameList: 	{numOfProblemsx1 cell}
% 			|   PictureTag:			char
% 			|   CombineOutPathRoot: chars
% 			|   ZDT1:				[1x1 struct]
% 			|   ZDT2:				[1x1 struct]
% 			|	SingleOutPathRoot:	chars
% 			|	PictureColor:		chars
% 			|	InProcessEvaluations:arrays
% 			|   .......
% 			|	.......
% 			|   testproblem n->>->>-+------------------------------------------------------------------------
% 	    				  			|	name: 					'testproblem n'										
%                					|	date: 					'xx:xx:xx'											
%               					|	bytes: 					0													
%               					|	isdir: 					1													
%     								|	datenum: 				xx													
%                					|	Path: 					'..\Experiment Data\xx\data\xx\testproblem n'		
%      	        					|	EP: 					[runtimex1 struct]>>-->>-->>-->>-->>-->>-->>-->>-->>+-------------------------------------------------------------------------
%     	         					|	ER: 					[runtimex1 struct]									|	name: 					'testproblem n'								 
%      	        					|	GD: 					[runtimex1x1 struct]								|	date: 					'xx:xx:xx'									
%       	       					|	HV: 					[runtimex1x1 struct]								|	bytes: 					0											
%      		       					|	IGD: 					[runtimex1x1 struct]								|	isdir: 					1											
%       	  						|	IGDPlus: 				[runtimex1x1 struct]								|	datenum: 				xx											
%       	   						|	SPREAD: 				[runtimex1x1 struct]								|	Path: 					'..\Experiment Data\xx\data\xx\testproblem\INPROCESSDATAN\ n'
%            						|	Best: 					[numOfIndicatorsx2x1 struct]						|	CIPDEpsOutPutPath：		
%     		 						|	Median: 				[numOfIndicatorsx2x1 struct]						|	CIPDFigOutPutPath:		
% 	 								|	InprocessDataPathList: 	[runtimex1x1 struct]								|	AllInCIPDFigOutPutPath:	
%   								|	IndicatorList: 			{Indicatorsx1 cell}									|	Path:					
% 									|	CombineOutPath:			chars												|	
% 									|	SingleOutPath:			chars												|
% 									|	IPDFigOutPathList:		[numOfRuntimex1 cell]								|
% 									|	IPDEpsOutPathList:		[numOfRuntimex1 cell]								|
% ------------------------------------------------------------------------------------------------------------------

%% ExperimentData: Main function
function algorithms = ExperimentData(INPUTexperimentRootDirectory, INPUTexperimentDataTag)


	global algorithms experimentRootDirectory experimentDataTag numOfAlgorithms numOfProblems numOfIndicators...
		numOfIPCD selectMatrix titleMatrix IndicatorMatrix;

	experimentRootDirectory = INPUTexperimentRootDirectory;
	experimentDataTag 		= INPUTexperimentDataTag;
	numOfAlgorithms 		= 8;
	numOfProblems  			= 16;
	numOfIPCD 				= 30;
	selectMatrix 			= {	'ZDT1',	'ZDT2', 'ZDT3',...
					   			'ZDT4', 'ZDT6', 'UF2' ,...
					   			'UF7',	'DTLZ1','DTLZ2',...
					   			 };
	titleMatrix				= {	'( a )','( b )','( c )','( d )','( e )','( f )',...
    							'( g )','( h )','( i )','( j )','( k )','( l )'};
    % 'EP','ER','GD','HV','IGD','IGDPlus','SPREAD'
    IndicatorMatrix 		= {'ER','IGD','SPREAD'};
	numOfIndicators 		= length(IndicatorMatrix);


	disp('Step1 : getAlgorithmList');
	getAlgorithmList();

	disp('Step2 : getTestProblemList');
	getTestProblemList();

	disp('Step3 : getIndicatorAndInProcessList');
	getIndicatorAndInProcessList();

	disp('Step4 : setPictureTag');
	setPictureTag();

	disp('Step5 : setPictureColor');
	setPictureColor();

	disp('Step6 : setPictureOutPath');
	setPictureOutPath();

	disp('Step7 : setInProcessEvaluations');
	setInProcessEvaluations();

	disp('Step8 : inProcessDataVisualize');
	% inProcessDataVisualize();

	disp('Step9 : resultDataVisualize');
	resultDataVisualize();

	disp('Step10 : combineDataVisualize');
	combineDataVisualize();

	disp(['Processing finish, you can find the fig and eps in destination folder : ',INPUTexperimentRootDirectory]);
	clear INPUTexperimentRootDirectory INPUTexperimentDataTag numOfAlgorithms numOfProblems numOfIndicators numOfIPCD;
end

%% getAlgorithmList:
function getAlgorithmList()

	global algorithms experimentRootDirectory experimentDataTag

	% get the experiment root directory
	experimentRootPath = fullfile(experimentRootDirectory, experimentDataTag);
	experimentSubRootDirectoryList = dir(experimentRootPath);
	if length(experimentSubRootDirectoryList) < 1
		error('The input params is wrong or the experimentRootDir is not exists')
	end

	% set data root dir path
	% if not exits, error will be throw
	experimentDataRootPath = fullfile(experimentRootPath,'data');
	if  length(dir(experimentDataRootPath)) < 1
		error('experimentDataRootPath is not exists');
	end

	% set latex scripts root dir path
	experimentLatexRootPath = fullfile(experimentRootPath,'latex');
	if  length(dir(experimentLatexRootPath)) < 1
		warning('experimentLatexRootPath is not exists');
	end

	% set data root dir path
	experimentRScriptRootPath = fullfile(experimentRootPath,'R');
	if  length(dir(experimentRScriptRootPath)) < 1
		warning('experimentRScriptRootPath is not exists');
	end

	experimentDataRootPathDirOutput = dir(experimentDataRootPath);
	% remove the . and .. directory
	algorithms = experimentDataRootPathDirOutput(3:end);
	% set path value
	for i = 1:length(algorithms)
		algorithms(i).Path = fullfile(experimentDataRootPath, ...
				char(algorithms(i).name));
	end

	clear experimentRootPath experimentSubRootDirectoryList experimentDataRootPath ...
	 experimentLatexRootPath experimentRScriptRootPath experimentDataRootPathDirOutput...
	 i;
end

%% getTestProblemList: 
function getTestProblemList()
	
	global algorithms numOfAlgorithms;

	% get number of algorithms in this experiment

	for i = 1:numOfAlgorithms
		testProblemDirOutput = dir(algorithms(i).Path);
		% remove the . and .. directory
		testProblemNameList = testProblemDirOutput(3:end);
		testProblemList = {testProblemNameList.name};

		% set path value
		for j = 1:length(testProblemNameList)
			testProblemNameList(j).Path = fullfile(algorithms(i).Path, ...
					char(testProblemNameList(j).name));
			eval(['[algorithms(', num2str(i) , ').', ...
							char(testProblemNameList(j).name),...
							 '] = deal(testProblemNameList(',num2str(j),'));']);
		end

		algorithms(i).ProblemNameList = testProblemList';
		clear testProblemDirOutput testProblemNameList testProblemList;
	end
	clear i j;
end

%% getInProcessDataPath:
function getIndicatorAndInProcessList()

	global algorithms numOfAlgorithms numOfProblems IndicatorMatrix;

	% get number of algorithms in this experiment

	for i = 1:numOfAlgorithms
		% get number of Problems in this algorithm
		tempProblemsNameList = algorithms(i).ProblemNameList;
		% get problem object list
		tempProblems = [];
		for j = 1:length(tempProblemsNameList)
			eval(['tempProblems = [tempProblems;algorithms(', num2str(i),').',char(tempProblemsNameList(j)),'];']);
		end
		clear j;

		for j = 1:numOfProblems
			% get list of experiment data in this algorithm, corresponding to this problem
			tempProblemDirOutput = dir(tempProblems(j).Path);
			tempProblemDirOutput = tempProblemDirOutput(3:end);
			tempIndicatorList = [];
			tempIndicatorPathList = [];
			tempInProcessDataPathList = [];

			for k = 1:length(tempProblemDirOutput)

				% If file is directory, it will be set as directory
				if (tempProblemDirOutput(k).isdir == 1)
					tempInProcessDataPath = tempProblemDirOutput(k);
					tempInProcessDataPath.Path = fullfile(tempProblems(j).Path,tempInProcessDataPath.name);
					tempInProcessDataPathList = [tempInProcessDataPathList;tempInProcessDataPath];             
                    
				% If file name does not endwith '.tsv', it will be named as an indicator
				% this name will be add to the tempIndicatorList
				elseif length(tempProblemDirOutput(k).name) < 4 ...
					|| ~strcmp(tempProblemDirOutput(k).name(end-3:end), '.tsv')
					
                    tempIndicatorName = tempProblemDirOutput(k).name;

                    % the IGD+ name may occur some error in matlab
                    % IGD+ is repalced by IGDPlus now
                    if isempty(IndicatorMatrix(strcmp(IndicatorMatrix, tempIndicatorName)))
                    	continue;
                	end
					tempIndicatorList = [tempIndicatorList ; cellstr(tempIndicatorName)];
					temp = dir(fullfile(tempProblems(j).Path,tempIndicatorName));
					temp.Path = fullfile(tempProblems(j).Path,tempIndicatorName);

					tempIndicatorPathList = [tempIndicatorPathList ;temp];
					tempIndicatorDataList = dir(fullfile(tempProblems(j).Path,...
											['*',tempProblemDirOutput(k).name,'.tsv']));
                    tempIndicatorDataIndex = [];
                    for v = 1:length(tempIndicatorDataList)
                        if cell2mat(regexp({tempIndicatorDataList(v).name},...
                                ['[0-9]',tempProblemDirOutput(k).name,'.tsv$'])) > 0
                            tempIndicatorDataIndex = [tempIndicatorDataIndex;v];
                        end
                    end
                    clear v;
                    
					tempIndicatorDataList = tempIndicatorDataList(tempIndicatorDataIndex);
					% add field name to algorithms(i).ProblemPath struct
					eval(['[algorithms(', num2str(i) , ').', char(tempProblems(j).name) ,'.', ...
							tempIndicatorName, '] = deal(tempIndicatorDataList);']);
			
				% pass
                elseif strcmp(tempProblemDirOutput(k).name(end-3:end), '.tsv')
                	% delete IGD+ file
                    if length(tempProblemDirOutput(k).name) >= 8
                    	if strcmp(tempProblemDirOutput(k).name(end-7:end), 'IGD+.tsv')
                    		delete(fullfile(tempProblems(j).Path,tempProblemDirOutput(k).name));
                		end
                	end
				else
					warning('The file name format does not match!');
                end
                
				clear tempInProcessDataPath tempIndicatorName tempIndicatorDataList tempIndicatorDataIndex temp;
			end
			eval(['[algorithms(', num2str(i) , ').', char(tempProblems(j).name) ,'.Best', ...
							'] = dir(fullfile(tempProblems(',num2str(j) ,').Path,''BEST*''));']);
			eval(['[algorithms(', num2str(i) , ').', char(tempProblems(j).name) ,'.Median', ...
							'] = dir(fullfile(tempProblems(',num2str(j) ,').Path,''MEDIAN*''));']);
			eval(['[algorithms(', num2str(i) , ').', char(tempProblems(j).name) ,'.InprocessDataPathList', ...
							'] = deal(tempInProcessDataPathList);']);
			eval(['[algorithms(', num2str(i) , ').', char(tempProblems(j).name) ,'.IndicatorList', ...
							'] = deal(tempIndicatorList);']);
			eval(['[algorithms(', num2str(i) , ').', char(tempProblems(j).name) ,'.IndicatorPathList', ...
							'] = deal(tempIndicatorPathList);']);

			clear tempProblemDirOutput tempIndicatorList tempInProcessDataPathList tempIndicatorPathList;
		end
		clear tempProblems tempProblemsNameList;
	end
	clear i j k;
end

%% setPictureTag:
function setPictureTag()

	global algorithms numOfAlgorithms;
	
	tags = ['oxsv^dph+*<>'];
	% get number of algorithms in this experiment

	if length(tags) < numOfAlgorithms
		error('the number of algorithms larger than the picture tag size, please modify it at function:setPictureTag');
	end

	for i = 1:numOfAlgorithms
		algorithms(i).PictureTag = tags(i);
	end

	clear tags i;
end

%% setPictureColor: 
function setPictureColor()
	
	global algorithms numOfAlgorithms;
	
	tags = ['bgrcmywk'];
	% get number of algorithms in this experiment

	if length(tags) < numOfAlgorithms
		error('the number of algorithms larger than the picture tag size, please modify it at function:setPictureTag');
	end

	for i = 1:numOfAlgorithms
		algorithms(i).PictureColor = tags(i);
	end

	clear tags i;
end

%% setInProcessEvaluations
function setInProcessEvaluations()

	global algorithms numOfAlgorithms;
	
	tags = [2];
	tags = [tags,[10:10:500]];
	% get number of algorithms in this experiment

	if length(tags) < numOfAlgorithms
		error('the number of algorithms larger than the picture tag size, please modify it at function:setPictureTag');
	end

	for i = 1:numOfAlgorithms
		algorithms(i).InProcessEvaluations = tags;
	end

	clear tags i;
end

%% setPictureOutPath: 
function setPictureOutPath()

	global algorithms experimentRootDirectory experimentDataTag numOfAlgorithms ...
		numOfProblems;
	
	% get number of algorithms in this experiment
	ProblemNameList = algorithms.ProblemNameList;

	for i = 1:numOfAlgorithms

		% combine different algorithm data into one pics
		% this operator is to set the pics store path
		algorithms(i).CombineOutPathRoot = fullfile(experimentRootDirectory,experimentDataTag,'CombineOutPathRoot');
		if isempty(dir(algorithms(i).CombineOutPathRoot))
			[SUCCESS,MESSAGE,~] = mkdir(algorithms(i).CombineOutPathRoot);
			if SUCCESS
				disp([algorithms(i).CombineOutPathRoot, ' is created successfully']);
			else
				error(MESSAGE);
			end
		else
			disp([algorithms(i).CombineOutPathRoot, ' is exists']);
		end
		for j = 1:numOfProblems
				
			eval(['CombineOutPath = fullfile(''',algorithms(i).CombineOutPathRoot,''',''',...
						char(algorithms(i).ProblemNameList(j)),''');'])
			eval(['[algorithms(', num2str(i) , ').', char(algorithms(i).ProblemNameList(j)) ,'.CombineOutPath', ...
						'] = deal(CombineOutPath);']);
			if isempty(dir(CombineOutPath))
				[SUCCESS,MESSAGE,~] = mkdir(CombineOutPath);
				if SUCCESS
					disp([CombineOutPath, ' is created successfully']);
				else
					error(MESSAGE);
				end
				clear SUCCESS MESSAGE MESSAGEID;
			else
				disp([CombineOutPath, ' is exists']);
			end
			clear CombineOutPath;
		end


		% process different algorithm data into different pics
		% this operator is to set the pics store path
		algorithms(i).SingleOutPathRoot = fullfile(experimentRootDirectory,experimentDataTag,...
											'SingleOutPathRoot',char(algorithms(i).name));
		if isempty(dir(algorithms(i).SingleOutPathRoot))
			[SUCCESS,MESSAGE,~] = mkdir(algorithms(i).SingleOutPathRoot);
			if SUCCESS
				disp([algorithms(i).SingleOutPathRoot, ' is created successfully']);
			else
				error(MESSAGE);
			end
		else
			disp([algorithms(i).SingleOutPathRoot, ' is exists']);
		end
		for j = 1:numOfProblems
				
			eval(['SingleOutPath = fullfile(''',algorithms(i).SingleOutPathRoot,''',''',...
						char(algorithms(i).ProblemNameList(j)),''');'])
			eval(['[algorithms(', num2str(i) , ').', char(algorithms(i).ProblemNameList(j)) ,'.SingleOutPath', ...
						'] = deal(SingleOutPath);']);
			if isempty(dir(SingleOutPath))
				[SUCCESS,MESSAGE,~] = mkdir(SingleOutPath);
				if SUCCESS
					disp([SingleOutPath, ' is created successfully']);
				else
					error(MESSAGE);
				end
				clear SUCCESS MESSAGE MESSAGEID;
			else
				disp([SingleOutPath, ' is exists']);
			end

			clear SingleOutPath;
		end

		% process different algorithm data into different pics
		% this operator is to set the pics store path
		algorithms(i).AllInOutPathRoot = fullfile(experimentRootDirectory,experimentDataTag,...
											'AllInOutPathRoot');
		if isempty(dir(algorithms(i).AllInOutPathRoot))
			[SUCCESS,MESSAGE,~] = mkdir(algorithms(i).AllInOutPathRoot);
			if SUCCESS
				disp([algorithms(i).AllInOutPathRoot, ' is created successfully']);
			else
				error(MESSAGE);
			end
		else
			disp([algorithms(i).AllInOutPathRoot, ' is exists']);
		end
	end

	clear i j SUCCESS MESSAGE MESSAGEID tempProblems;
end

%% inProcessDataVisualize: 
function inProcessDataVisualize()

	global algorithms numOfAlgorithms numOfProblems;


	% loop for every algorithm
	for i = 1:numOfAlgorithms

		% get problem object list
		problems = [];
		for j = 1:numOfProblems
			eval(['problems = [problems;algorithms(', num2str(i),').',char(algorithms(i).ProblemNameList(j)),'];']);
		end
		clear j;

		% loop for every test problem
		for j = 1:numOfProblems

			numOfInProcessDataDirectory = length(problems(j).InprocessDataPathList);
			figFilesOutPathList			= cell(size(numOfInProcessDataDirectory));
			epsFilesOutPathList			= cell(size(numOfInProcessDataDirectory));

			% loop for every inProcessData directroy
			for k = 1: numOfInProcessDataDirectory

				inProcessDataFilesList = dir(fullfile(problems(j).InprocessDataPathList(k).Path, '*FUN*.tsv'));
				inProcessDataPicOutPutPath = fullfile(problems(j).SingleOutPath, problems(j).InprocessDataPathList(k).name);
				inProcessDataInputRootPath = problems(j).InprocessDataPathList(k).Path;

				% combine all inProcess Data into one pics
				% return isSuccess

				[isSuccess, figFilePath, epsFilePath] = inProcessDataToPics(inProcessDataFilesList,...
														 inProcessDataPicOutPutPath, inProcessDataInputRootPath);

				if isSuccess
					figFilesOutPathList(k) = cellstr(figFilePath);
					epsFilesOutPathList(k) = cellstr(epsFilePath);
					disp(['inProcess data ', inProcessDataInputRootPath , ' visualize successfully']);
				else
					disp('sth. wrong occured in ', inProcessDataInputRootPath ,' please fix it in function:inProcessDataToPics');
				end

				clear inProcessDataFilesList inProcessDataInputRootPath ...
						inProcessDataPicOutPutPath isSuccess figFilePath epsFilePath;
			end

			% put variable into struct
			% IPD: In Process Data
			eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,'.IPDFigOutPathList', ...
							'] = deal(figFilesOutPathList'');']);
			eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,'.IPDEpsOutPathList', ...
							'] = deal(epsFilesOutPathList'');']);

			clear numOfInProcessDataDirectory figFilesOutPathList epsFilesOutPathList k;
		end

		clear problems j;
	end

	clear i;
end

%% resultDataVisualize: 
function resultDataVisualize()

	disp('Step 9.1: checkIndicator');
	isSuccess = checkIndicator();
	if (~isSuccess);
		error('check indicator file input and ouput path error');
	else
		disp('check indicator file input and ouput path successfully');
	end

	disp('Step 9.2: resultData2Boxplot');
	isSuccess = resultData2BoxPLot();
	if (~isSuccess);
		error('function resultData2Boxplot error');
	else
		disp('resultData2Boxplot successfully');
	end

	disp('Step 9.3: boxplot2One');
	isSuccess = boxplot2One();
	if (~isSuccess);
		error('function boxplot2One error');
	else
		disp('boxplot2One successfully');
	end

	clear i isSuccess;
end

%% combineDataVisualize: 
function combineDataVisualize()

	
	disp('Step 10.1: inProcessDataToTrendPic');
	isSuccess = inProcessDataToTrendPic();
	if (~isSuccess);
		error('inProcessDataToTrendPic file input and ouput path error');
	else
		disp('inProcessDataToTrendPic file input and ouput path successfully');
	end

	disp('disp 10.2: trendPics2One');
	isSuccess = trendPics2One();
	if (~isSuccess);
		error('trendPics2One file input and ouput path error');
	else
		disp('trendPics2One file input and ouput path successfully');
	end
	
	clear isSuccess;
end

%% inProcessDataToPics: private inner function
function [isSuccess, figFilePath, epsFilePath] = inProcessDataToPics(inProcessDataFilesList, inProcessDataPicOutPutPath,inProcessDataInputRootPath)
	
	isSuccess 		= false;
	isClearCache 	= false;
	isFigFileExists = false;
	isEpsFileExists = false;
	numOfFiles 		= length(inProcessDataFilesList);
	figFilePath 	= [inProcessDataPicOutPutPath,'.fig'];
	epsFilePath 	= [inProcessDataPicOutPutPath,'.eps'];
	figPosition 	= [0 0 20 20];

	if ~isempty(dir(figFilePath))
		isFigFileExists = true;
	end
	if ~isempty(dir(epsFilePath))
		isEpsFileExists = true;
	end
	if isClearCache
		if isFigFileExists
			delete(figFilePath);
			disp([figFilePath, ' is delete']);
		end
		if isEpsFileExists
			delete(epsFilePath);
			disp([epsFilePath, ' is delete']);
		end
	elseif isFigFileExists && isEpsFileExists
		isSuccess = true;
		return;
	end

	% loop for every inProcessData file in one inProcessData directory 
	figure('visible','off');
	set(gcf,'units','centimeters');
    set(gcf,'position',figPosition);
	hold on;
	for i = 1:numOfFiles

		tempInProcessData 	= importdata(fullfile(inProcessDataInputRootPath,...
								inProcessDataFilesList(i).name));
		dimensionSize 		= size(tempInProcessData,2);
		iterationNumber		= str2num(inProcessDataFilesList(i).name...
								(regexp(inProcessDataFilesList(i).name,'\d')));

		if (mod(iterationNumber,5) ~= 0)
			clear tempInProcessData dimensionSize iterationNumber;
			continue;
		end

		% figure operator
		if dimensionSize == 2
			
			plot(tempInProcessData(:,1), tempInProcessData(:,2), ...
				'ro', 'MarkerFace', 'r', 'MarkerSize', 2);
			xlabel('1^{st} Obejctive');
    		ylabel('2^{nd} Obejctive');
    		set(get(gca,'XLabel'),'FontSize',16);
            set(get(gca,'YLabel'),'FontSize',16);

		elseif dimensionSize == 3

			plot3(tempInProcessData(:,1), tempInProcessData(:,2), ...
				tempInProcessData(:,3), 'ro', 'MarkerFace', 'r', 'MarkerSize', 2);
			xlabel('1^{st} Obejctive');
	    	ylabel('2^{nd} Obejctive');
	    	zlabel('3^{rd} Obejctive');
	    	set(get(gca,'XLabel'),'FontSize',16);
    		set(get(gca,'YLabel'),'FontSize',16);
            set(get(gca,'ZLabel'),'FontSize',16);
	    	view(-243,29);
	    	
		elseif dimensionSize > 3

			for j = 1:dimensionSize
	    		plot(tempInProcessData(j,:));
	    	end
	    	xlabel('Obejctive Number');
	    	ylabel('Objective Value');
	    	set(gca, 'XTick', [1:1:dimensionSize]);
	    	clear j;
		end

		hold on;
		clear tempInProcessData dimensionSize iterationNumber;
	end

	% figure params set and save	 
    set(gcf,'color','w');
    title('Objective Value in Process');
    legend('Archive');
    grid on;
    axis square;
    hold off;
	saveas(gcf, figFilePath);
	disp([figFilePath, ' is saved']);
	print(gcf, '-deps', '-opengl', '-r600', epsFilePath);
	disp([epsFilePath, ' is saved']);
    close('all');
    isSuccess = true;
    clear gcf isFigFileExists isEpsFileExists;
end

%% checkIndicator
function isSuccess = checkIndicator()

	global algorithms numOfAlgorithms numOfProblems numOfIndicators;

	isSuccess		= false;
    isClearCache    = false;

    % maybe removed in genral use
    % load('matlab.mat');
    % isSuccess = true;
    % algorithms = algs;
    % setInProcessEvaluations();
    % clear algs;
    % return;

	% loop for every algorithm
	for i = 1:numOfAlgorithms

		% get problem object list
		problems = [];
		for j = 1:numOfProblems
			eval(['problems = [problems;algorithms(', num2str(i),').',char(algorithms(i).ProblemNameList(j)),'];']);
		end
		clear j;

		% loop for every test problem
		for j = 1:numOfProblems

			if (length(problems(1).InprocessDataPathList) ~= length(problems(j).InprocessDataPathList))
				error(['lack of experiment inprocess data in problem: ',...
				 problems(j).name, ' and in algorithm: ',algorithms(i).name]);
			end
			if (length(problems(1).IndicatorList) ~= length(problems(j).IndicatorList))
				error(['lack of experiment inprocess indicator data in problem: ',...
				 problems(j).name, ' and in algorithm: ',algorithms(i).name]);
			end

			indicatorList = problems(j).IndicatorList;

			% loop for every inProcessData directroy
			for v = 1: numOfIndicators

				eval(['inProcessData = problems(',num2str(j),...
					').',char(indicatorList(v)),';']);
				numOfInProcessDataDirectory = length(inProcessData);

				RDFigOutPath = fullfile(problems(j).CombineOutPath,...
								[char(indicatorList(v)),'.fig']);
				RDFigOutPath_SingleOut = fullfile(problems(j).CombineOutPath,...
								[char(indicatorList(v)),'_SingleOut.fig']);
				RDEpsOutOutPath = fullfile(problems(j).CombineOutPath,...
								[char(indicatorList(v)),'.eps']);
				ALLInRDFigOutPath = fullfile(algorithms(i).AllInOutPathRoot,...
								[char(indicatorList(v)),'_ALLIn.fig']);

				for k = 1: numOfInProcessDataDirectory


					CIPDEpsOutPutPath = fullfile(problems(j).CombineOutPath,...
								[inProcessData(k).name(1:end-4), '.eps']);
					CIPDFigOutPutPath = fullfile(problems(j).CombineOutPath,...
								[inProcessData(k).name(1:end-4), '.fig']);
					CIPDFigOutPutPath_SingleOut = fullfile(problems(j).CombineOutPath,...
								[inProcessData(k).name(1:end-4), '_SingleOut.fig']);
					AllInCIPDFigOutPutPath = fullfile(algorithms(i).AllInOutPathRoot,...
								[inProcessData(k).name(1:end-4), '_ALLIn.fig']);
					CIPDInputPath = fullfile(problems(j).Path,...
								[inProcessData(k).name]);
					
                    if isClearCache && (i == 1)
                    	if ~isempty(dir(CIPDFigOutPutPath))
                        	delete(CIPDFigOutPutPath);
                        	disp([CIPDFigOutPutPath,' is delele successfully']);
                        end
                        if ~isempty(dir(CIPDFigOutPutPath_SingleOut))
                        	delete(CIPDFigOutPutPath_SingleOut);
                        	disp([CIPDFigOutPutPath_SingleOut,' is delele successfully']);
                        end
                        if ~isempty(dir(CIPDEpsOutPutPath))
                        	delete(CIPDEpsOutPutPath);
                        	disp([CIPDEpsOutPutPath,' is delele successfully']);
                        end
                        if ~isempty(dir(AllInCIPDFigOutPutPath))
                        	delete(AllInCIPDFigOutPutPath);
                        	disp([AllInCIPDFigOutPutPath,' is delele successfully']);
                        end
                    end
                    
					% put variable into struct
					% CIPD: combine In Process Data
					eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.',char(indicatorList(v)),'(',num2str(k),').EpsOutPutPath',...
								'] = deal(CIPDEpsOutPutPath);']);
					eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.',char(indicatorList(v)),'(',num2str(k),').FigOutPutPath',...
								'] = deal(CIPDFigOutPutPath);']);
					eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.',char(indicatorList(v)),'(',num2str(k),').CIPDFigOutPutPath_SingleOut',...
								'] = deal(CIPDFigOutPutPath_SingleOut);']);
					eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.',char(indicatorList(v)),'(',num2str(k),').AllInCIPDFigOutPutPath',...
								'] = deal(AllInCIPDFigOutPutPath);']);
					eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.',char(indicatorList(v)),'(',num2str(k),').Path',...
								'] = deal(CIPDInputPath);']);

					clear CIPDEpsOutPutPath CIPDFigOutPutPath CIPDInputPath AllInCIPDFigOutPutPath ...
						CIPDFigOutPutPath_SingleOut;
				end

				if isClearCache && (i == 1)
                    if ~isempty(dir(RDFigOutPath_SingleOut))
                    	delete(RDFigOutPath_SingleOut);
                    	disp([RDFigOutPath_SingleOut,' is delele successfully']);
                    end
                    if ~isempty(dir(RDFigOutPath))
                    	delete(RDFigOutPath);
                    	disp([RDFigOutPath,' is delele successfully']);
                    end
                    if ~isempty(dir(RDEpsOutOutPath))
                    	delete(RDEpsOutOutPath);
                    	disp([RDEpsOutOutPath,' is delele successfully']);
                    end 
                    if ~isempty(dir(ALLInRDFigOutPath))
                    	delete(ALLInRDFigOutPath);
                    	disp([ALLInRDFigOutPath,' is delele successfully']);
                    end 
                    if ~isempty(dir([ALLInRDFigOutPath(1:end-4),'.eps']))
                    	delete([ALLInRDFigOutPath(1:end-4),'.eps']);
                    	disp([ALLInRDFigOutPath(1:end-4),'.eps is delete successfully']);
                	end
                end

				eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.IndicatorPathList(',num2str(v),').RDFigOutPath',...
								'] = deal(RDFigOutPath);']);
				eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.IndicatorPathList(',num2str(v),').RDFigOutPath_SingleOut',...
								'] = deal(RDFigOutPath_SingleOut);']);
				eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.IndicatorPathList(',num2str(v),').RDEpsOutOutPath',...
								'] = deal(RDEpsOutOutPath);']);
				eval(['[algorithms(', num2str(i) , ').', char(problems(j).name) ,...
								'.IndicatorPathList(',num2str(v),').ALLInRDFigOutPath',...
								'] = deal(ALLInRDFigOutPath);']);

				clear k inProcessData numOfInProcessDataDirectory RDFigOutPath_SingleOut ...
					RDFigOutPath RDEpsOutOutPath ALLInRDFigOutPath;
			end

			clear v indicatorList;
		end

		clear problems j;
	end

	isSuccess = true;
	clear i isClearCache;
end

%% inProcessDataToTrendPic:
function isSuccess = inProcessDataToTrendPic()
	
	global algorithms numOfAlgorithms numOfIndicators numOfProblems numOfIPCD IndicatorMatrix;
    
	isSuccess		= false;
	figPosition		= [0 0 20 20];
	eval(['indicatorList = algorithms(1).',char(algorithms(1).ProblemNameList(1)),...
		'.IndicatorList;']);

	for i = 1:numOfProblems
		for j = 1: numOfIndicators
			for k = 1:numOfIPCD

				eval(['CIPDFigOutPutPath = algorithms(1).',...
					char(algorithms(1).ProblemNameList(i)),...
					'.',char(indicatorList(j)),'(',num2str(k),...
					').FigOutPutPath;']);
				eval(['CIPDEpsOutPutPath = algorithms(1).',...
					char(algorithms(1).ProblemNameList(i)),...
					'.',char(indicatorList(j)),'(',num2str(k),...
					').EpsOutPutPath;']);
				eval(['CIPDFigOutPutPath_SingleOut = algorithms(1).',...
					char(algorithms(1).ProblemNameList(i)),...
					'.',char(indicatorList(j)),'(',num2str(k),...
					').CIPDFigOutPutPath_SingleOut;']);

				figure('visible','off');
				set(gcf,'units','centimeters');
				set(gcf,'position',figPosition);
				hold on;

				for v = 1:numOfAlgorithms

					eval(['CIPDInputPath = algorithms(',num2str(v),...
						').',char(algorithms(v).ProblemNameList(i)),...
						'.',char(indicatorList(j)),'(',num2str(k),').Path;']);

					disp(['importdata from ',CIPDInputPath]);
					data 				= importdata(CIPDInputPath);
					[~, index]			= sort(data(:,1));
					data 				= data(index,:);
					popNumber 			= getPopNumber(char(algorithms(v).ProblemNameList(i)));
					plot((algorithms(v).InProcessEvaluations(1:size(data,1)))*popNumber,...
						data(:,2),['k',algorithms(v).PictureTag,'-'],...
						'MarkerSize',2);
					hold on;

					clear data ttempData index CIPDInputPath;
				end

				leg = legend({algorithms.name});
				set(leg,'FontSize',5);
				saveas(gcf,CIPDFigOutPutPath);
				disp(['save ',CIPDFigOutPutPath,' successfully']);
				set(gcf,'color','w');
			    title([char(indicatorList(j)),' for ',...
			    	char(algorithms(1).ProblemNameList(i))]);
			    xlabel('FEAS','FontSize',16);
			    ylabel(char(indicatorList(j)),'FontSize',16);
			    grid on;
			    axis square;
			    hold off;
				saveas(gcf, [CIPDFigOutPutPath_SingleOut]);
				disp(['save ',CIPDFigOutPutPath_SingleOut,' successfully']);
				print(gcf, '-deps', '-opengl', '-r600', CIPDEpsOutPutPath);
				disp(['save ',CIPDEpsOutPutPath,' successfully']);
				hold off;
				close('all');
				clear v leg CIPDFigOutPutPath CIPDEpsOutPutPath CIPDFigOutPutPath_SingleOut;
			end
		
			clear k;
		end

		clear j;
	end

	isSuccess = true;
	clear i figPosition indicatorList;
end

%% trendPics2One:
% subplot size is [4,3], so the max problem length is 12 in one pic
function isSuccess = trendPics2One()
	global  numOfIndicators numOfIPCD algorithms titleMatrix selectMatrix IndicatorMatrix;

	fontSize 		= '5';
	fontName 		= 'Times New Roman';

	for k = 1:numOfIPCD

		for j = 1:numOfIndicators

			clc;
			figOut = figure('visible','off');

			for i = 1:length(selectMatrix)

				problemName = char(selectMatrix(i));
				eval(['indicatorName = char(algorithms(1).',problemName,'.IndicatorPathList(',num2str(j),').name);'])
				eval(['CIPD = algorithms(1).',problemName,'.',indicatorName,'(',num2str(k),');']);
				CIPDFigOutPutPath = CIPD.FigOutPutPath;
				AllInCIPDFigOutPutPath = CIPD.AllInCIPDFigOutPutPath;
				if ~isempty(dir(AllInCIPDFigOutPutPath))
					delete(AllInCIPDFigOutPutPath);
					disp([AllInCIPDFigOutPutPath,'is deleted successfully']);
				end
				disp(['openfig from ',CIPDFigOutPutPath]);
				eval([problemName,'FigHandle = openfig(''',CIPDFigOutPutPath,''',''invisible'',''reuse'');']);
				% eval([problemName,'LegendHandle = legend;']);
				% eval([problemName,'LegendHandle.FontSize = 4;']);
				% eval([problemName,'LegendHandle.FontName = ''',fontName,''';']);
				% eval([problemName,'LegendHandle.Color = ''none'';']);
				eval([problemName,'AxeHandle = gca;']);
				eval([problemName,'AxeHandle.Color = ''none'';']);
				eval([problemName,'FigHandle.Color = ''none'';']);
				eval([problemName,'FigHandle.InvertHardcopy = ''off'';']);
				eval([problemName,'AxeHandle.XLabel.String = {''FEAS 10^{4}'',''',char(titleMatrix(i)),' ',...
									indicatorName ,' for ',problemName,'''};']);
				eval([problemName,'AxeHandle.YLabel.String = ''',indicatorName,''';']);
				eval([problemName,'AxeHandle.FontSize = ',fontSize,';']);
				eval([problemName,'AxeHandle.FontName = ''',fontName,''';']);
				eval(['YLim = ',problemName,'AxeHandle.YLim;']);
				if (YLim(end) > 2);
					YLim = [0,2];
				end
				eval([problemName,'AxeHandle.YLim = ',mat2str(YLim),';']);
				eval([problemName,'Copy = copyobj(',...
						problemName,'AxeHandle,figOut);']);
				eval(['subplot(4,3,',num2str(i),',',problemName,'Copy(1));']);

				eval(['close(',problemName,'FigHandle);']);
				eval(['clear ',problemName,'FigHandle ',problemName,'AxeHandle ',...
					problemName,'LegendHandle ',problemName,'Copy;']);
				clear problemName indicatorName CIPD CIPDFigOutPutPath YLim;

			end

			disp(['start generating ',AllInCIPDFigOutPutPath]);

			set(gcf,'Color','none');
			set(gca,'Color','none');
			set(gcf,'InvertHardcopy','off');
			set(gcf,'Position', get(0,'ScreenSize'));
			saveas(gcf,AllInCIPDFigOutPutPath);
			disp(['save ',AllInCIPDFigOutPutPath,' successfully']);
			print(gcf,'-deps','-r600',[AllInCIPDFigOutPutPath(1:end-4),'.eps']);
			disp(['save ',AllInCIPDFigOutPutPath,'.eps successfully']);
			close('all');
			clear i figOut gcf AllInCIPDFigOutPutPath;
		end

		clear j;
	end
	isSuccess = true;
	clear k fontSize;
end

%% resultData2Boxplot: function description
function isSuccess = resultData2BoxPLot()

	global algorithms numOfAlgorithms numOfIndicators numOfIPCD numOfProblems IndicatorMatrix;
	isSuccess		= false;
	FontSize 		= 7;
	figPosition		= [0 0 20 20];

	for i = 1:numOfProblems
		for j = 1:numOfIndicators

			figOut = figure('visible','off');
			set(gcf,'units','centimeters');
    		set(gcf,'position',figPosition);
			RD = zeros(numOfIPCD,numOfAlgorithms);
			XTickLabel = cell(1,numOfAlgorithms);
			clc;
			for k = 1:numOfAlgorithms
				eval(['RDFigOutPath = algorithms(',num2str(k),...
					').',char(algorithms(k).ProblemNameList(i)),...
					'.IndicatorPathList(',num2str(j),').RDFigOutPath;']);
				eval(['RDFigOutPath_SingleOut = algorithms(',num2str(k),...
					').',char(algorithms(k).ProblemNameList(i)),...
					'.IndicatorPathList(',num2str(j),').RDFigOutPath_SingleOut;']);
				eval(['RDInputPath = algorithms(',num2str(k),...
					').',char(algorithms(k).ProblemNameList(i)),...
					'.IndicatorPathList(',num2str(j),').Path;']);
				eval(['indicatorName = algorithms(',num2str(k),...
					').',char(algorithms(k).ProblemNameList(i)),...
					'.IndicatorPathList(',num2str(j),').name;']);

				disp(['importdata from',RDInputPath]);
				RD(:,k) 			= importdata(RDInputPath);
				XTickLabel(k) 		= {algorithms(k).name};

				clear RDInputPath;
			end

			disp(['start generating ',RDFigOutPath]);
			boxplot(RD);
			set(gcf,'color','w');
			set(gca,'XTickLabel',XTickLabel);
			set(gca,'FontSize',FontSize);
			saveas(gcf,[RDFigOutPath]);
			disp(['save ',RDFigOutPath,' successfully']);
			xlabel('algorithm Name');
			ylabel(indicatorName);
			title(['boxplot for ',indicatorName]);
			saveas(gcf,[RDFigOutPath_SingleOut]);
			disp(['save ',RDFigOutPath_SingleOut,' successfully']);
			clear k figOut RDFigOutPath RD RDFigOutPath_SingleOut indicatorName;
		end

		clear j;
	end

	isSuccess 		= true;
	clear i figPosition;
end

%% boxplot2One:
function isSuccess = boxplot2One()

	global algorithms numOfIndicators selectMatrix titleMatrix IndicatorMatrix;

	fontSize 		= '5';
	isSuccess 		= false;
	fontName 		= 'Times New Roman';

	for j = 1:numOfIndicators

		eval(['indicatorList = algorithms(1).',char(algorithms(1).ProblemNameList(1)),...
			'.IndicatorPathList;']);
		ALLInRDFigOutPath 	= indicatorList(j).ALLInRDFigOutPath;
		indicatorName 		= char(indicatorList(j).name);
		counter 			= 1;

		if ~isempty(dir(ALLInRDFigOutPath))
			delete(ALLInRDFigOutPath);
			disp([ALLInRDFigOutPath,'is deleted successfully']);
		end

		clc;
		disp(['start generating ', ALLInRDFigOutPath]);
		figOut = figure('visible','off');
		for i = 1:length(selectMatrix)

			problemName 	= char(selectMatrix(i));
			eval(['RDFigOutPath = algorithms(1).',problemName,...
				'.IndicatorPathList(',num2str(j),').RDFigOutPath;']);

			disp(['openfig from ',RDFigOutPath]);
			eval([problemName,'FigHandle = openfig(''',RDFigOutPath,''',''invisible'',''reuse'');']);
			eval([problemName,'AxeHandle = gca;']);
			eval([problemName,'AxeHandle.Color = ''none'';']);
			eval([problemName,'FigHandle.Color = ''none'';']);
			eval([problemName,'FigHandle.InvertHardcopy = ''off'';']);
			eval([problemName,'AxeHandle.XLabel.String = ''',char(titleMatrix(i)),' ',...
								indicatorName ,' Boxplot for ',problemName,''';']);
			eval([problemName,'AxeHandle.YLabel.String = ''',indicatorName,''';']);
			eval([problemName,'AxeHandle.XTickLabelRotation = 40;']);
			eval([problemName,'AxeHandle.FontSize = ',fontSize,';']);
			eval([problemName,'AxeHandle.FontName = ''',fontName,''';']);
			eval([problemName,'Copy = copyobj(',...
					problemName,'AxeHandle,figOut);']);
			eval(['subplot(4,3,',num2str(i),',',problemName,'Copy(1));']);

			eval(['close(',problemName,'FigHandle);']);
			eval(['clear ',problemName,'FigHandle ',problemName,'AxeHandle ',...
					problemName,'LegendHandle ',problemName,'Copy;'])
			clear problemName RDFigOutPath;
		end

		set(gcf,'Color','none');
		set(gca,'Color','none');
		set(gcf,'InvertHardcopy','off');
		set(gcf,'Position', get(0,'ScreenSize'));
		saveas(gcf,ALLInRDFigOutPath);
		disp(['save ',ALLInRDFigOutPath,' successfully']);
		print(gcf,'-deps','-r600',[ALLInRDFigOutPath(1:end-4),'.eps']);
		disp(['save ',ALLInRDFigOutPath(1:end-4),'.eps successfully']);
		close('all');

		clear i ALLInRDFigOutPath indicatorName indicatorList gcf;
	end

	isSuccess = true;
	clear j;
end

%% resultDataMean:
function isSuccess = resultDataMean()
end

%% getPopNumber:
function popNumber = getPopNumber(problemName)
	switch problemName
		case {'DTLZ1','DTLZ2','DTLZ3','DTLZ4','DTLZ5','DTLZ6','DTLZ7'}
			popNumber = 150;
		otherwise
			popNumber = 100;
	end
end