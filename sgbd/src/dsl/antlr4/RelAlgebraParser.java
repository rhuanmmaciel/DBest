package dsl.antlr4;// Generated from RelAlgebra.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RelAlgebraParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, SELECTION=9, 
		PROJECTION=10, JOIN=11, LEFT_JOIN=12, RIGHT_JOIN=13, UNION=14, CARTESIAN_PRODUCT=15, 
		INTERSECTION=16, SORT=17, GROUP=18, AGGREGATION=19, RENAME=20, IMPORT=21, 
		AS=22, RELATION=23, PREDICATE=24, DIGIT=25, PATH=26, THIS=27, WS=28;
	public static final int
		RULE_command = 0, RULE_importStatement = 1, RULE_nameDeclaration = 2, 
		RULE_pathStatement = 3, RULE_variableDeclaration = 4, RULE_createTable = 5, 
		RULE_expression = 6, RULE_position = 7, RULE_number = 8, RULE_selection = 9, 
		RULE_projection = 10, RULE_sort = 11, RULE_group = 12, RULE_rename = 13, 
		RULE_aggregation = 14, RULE_join = 15, RULE_leftJoin = 16, RULE_rightJoin = 17, 
		RULE_cartesianProduct = 18, RULE_union = 19, RULE_intersection = 20, RULE_unary = 21, 
		RULE_binary = 22, RULE_relation = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"command", "importStatement", "nameDeclaration", "pathStatement", "variableDeclaration", 
			"createTable", "expression", "position", "number", "selection", "projection", 
			"sort", "group", "rename", "aggregation", "join", "leftJoin", "rightJoin", 
			"cartesianProduct", "union", "intersection", "unary", "binary", "relation"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'.head'", "'='", "'<'", "','", "'>'", "'('", "')'", "'selection'", 
			"'projection'", "'join'", "'leftJoin'", "'rightJoin'", "'union'", "'cartesianProduct'", 
			"'intersection'", "'sort'", "'group'", "'aggregation'", "'rename'", "'import'", 
			"'as'", null, null, null, null, "'this.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "SELECTION", "PROJECTION", 
			"JOIN", "LEFT_JOIN", "RIGHT_JOIN", "UNION", "CARTESIAN_PRODUCT", "INTERSECTION", 
			"SORT", "GROUP", "AGGREGATION", "RENAME", "IMPORT", "AS", "RELATION", 
			"PREDICATE", "DIGIT", "PATH", "THIS", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "RelAlgebra.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RelAlgebraParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class CommandContext extends ParserRuleContext {
		public List<ImportStatementContext> importStatement() {
			return getRuleContexts(ImportStatementContext.class);
		}
		public ImportStatementContext importStatement(int i) {
			return getRuleContext(ImportStatementContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<CreateTableContext> createTable() {
			return getRuleContexts(CreateTableContext.class);
		}
		public CreateTableContext createTable(int i) {
			return getRuleContext(CreateTableContext.class,i);
		}
		public List<VariableDeclarationContext> variableDeclaration() {
			return getRuleContexts(VariableDeclarationContext.class);
		}
		public VariableDeclarationContext variableDeclaration(int i) {
			return getRuleContext(VariableDeclarationContext.class,i);
		}
		public CommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitCommand(this);
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_command);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(48);
				importStatement();
				}
				break;
			case 2:
				{
				setState(49);
				expression();
				}
				break;
			case 3:
				{
				setState(50);
				createTable();
				}
				break;
			case 4:
				{
				setState(51);
				variableDeclaration();
				}
				break;
			}
			setState(54);
			match(T__0);
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SELECTION) | (1L << PROJECTION) | (1L << JOIN) | (1L << LEFT_JOIN) | (1L << RIGHT_JOIN) | (1L << UNION) | (1L << CARTESIAN_PRODUCT) | (1L << INTERSECTION) | (1L << SORT) | (1L << GROUP) | (1L << AGGREGATION) | (1L << RENAME) | (1L << IMPORT) | (1L << RELATION))) != 0)) {
				{
				{
				setState(59);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(55);
					importStatement();
					}
					break;
				case 2:
					{
					setState(56);
					expression();
					}
					break;
				case 3:
					{
					setState(57);
					createTable();
					}
					break;
				case 4:
					{
					setState(58);
					variableDeclaration();
					}
					break;
				}
				setState(61);
				match(T__0);
				}
				}
				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImportStatementContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(RelAlgebraParser.IMPORT, 0); }
		public PathStatementContext pathStatement() {
			return getRuleContext(PathStatementContext.class,0);
		}
		public NameDeclarationContext nameDeclaration() {
			return getRuleContext(NameDeclarationContext.class,0);
		}
		public ImportStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterImportStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitImportStatement(this);
		}
	}

	public final ImportStatementContext importStatement() throws RecognitionException {
		ImportStatementContext _localctx = new ImportStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_importStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(IMPORT);
			setState(69);
			pathStatement();
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(70);
				nameDeclaration();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameDeclarationContext extends ParserRuleContext {
		public TerminalNode AS() { return getToken(RelAlgebraParser.AS, 0); }
		public TerminalNode RELATION() { return getToken(RelAlgebraParser.RELATION, 0); }
		public NameDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nameDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterNameDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitNameDeclaration(this);
		}
	}

	public final NameDeclarationContext nameDeclaration() throws RecognitionException {
		NameDeclarationContext _localctx = new NameDeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_nameDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(AS);
			setState(74);
			match(RELATION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PathStatementContext extends ParserRuleContext {
		public TerminalNode PATH() { return getToken(RelAlgebraParser.PATH, 0); }
		public TerminalNode THIS() { return getToken(RelAlgebraParser.THIS, 0); }
		public TerminalNode RELATION() { return getToken(RelAlgebraParser.RELATION, 0); }
		public PathStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pathStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterPathStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitPathStatement(this);
		}
	}

	public final PathStatementContext pathStatement() throws RecognitionException {
		PathStatementContext _localctx = new PathStatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_pathStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PATH:
				{
				setState(76);
				match(PATH);
				}
				break;
			case THIS:
				{
				{
				setState(77);
				match(THIS);
				setState(78);
				match(RELATION);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(81);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableDeclarationContext extends ParserRuleContext {
		public TerminalNode RELATION() { return getToken(RelAlgebraParser.RELATION, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitVariableDeclaration(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_variableDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			match(RELATION);
			setState(84);
			match(T__2);
			setState(85);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateTableContext extends ParserRuleContext {
		public TerminalNode RELATION() { return getToken(RelAlgebraParser.RELATION, 0); }
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public CreateTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createTable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterCreateTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitCreateTable(this);
		}
	}

	public final CreateTableContext createTable() throws RecognitionException {
		CreateTableContext _localctx = new CreateTableContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_createTable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(RELATION);
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(88);
				position();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public SelectionContext selection() {
			return getRuleContext(SelectionContext.class,0);
		}
		public ProjectionContext projection() {
			return getRuleContext(ProjectionContext.class,0);
		}
		public JoinContext join() {
			return getRuleContext(JoinContext.class,0);
		}
		public LeftJoinContext leftJoin() {
			return getRuleContext(LeftJoinContext.class,0);
		}
		public RightJoinContext rightJoin() {
			return getRuleContext(RightJoinContext.class,0);
		}
		public CartesianProductContext cartesianProduct() {
			return getRuleContext(CartesianProductContext.class,0);
		}
		public UnionContext union() {
			return getRuleContext(UnionContext.class,0);
		}
		public IntersectionContext intersection() {
			return getRuleContext(IntersectionContext.class,0);
		}
		public SortContext sort() {
			return getRuleContext(SortContext.class,0);
		}
		public GroupContext group() {
			return getRuleContext(GroupContext.class,0);
		}
		public AggregationContext aggregation() {
			return getRuleContext(AggregationContext.class,0);
		}
		public RenameContext rename() {
			return getRuleContext(RenameContext.class,0);
		}
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECTION:
				{
				setState(91);
				selection();
				}
				break;
			case PROJECTION:
				{
				setState(92);
				projection();
				}
				break;
			case JOIN:
				{
				setState(93);
				join();
				}
				break;
			case LEFT_JOIN:
				{
				setState(94);
				leftJoin();
				}
				break;
			case RIGHT_JOIN:
				{
				setState(95);
				rightJoin();
				}
				break;
			case CARTESIAN_PRODUCT:
				{
				setState(96);
				cartesianProduct();
				}
				break;
			case UNION:
				{
				setState(97);
				union();
				}
				break;
			case INTERSECTION:
				{
				setState(98);
				intersection();
				}
				break;
			case SORT:
				{
				setState(99);
				sort();
				}
				break;
			case GROUP:
				{
				setState(100);
				group();
				}
				break;
			case AGGREGATION:
				{
				setState(101);
				aggregation();
				}
				break;
			case RENAME:
				{
				setState(102);
				rename();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(106);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(105);
				position();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PositionContext extends ParserRuleContext {
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public PositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_position; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterPosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitPosition(this);
		}
	}

	public final PositionContext position() throws RecognitionException {
		PositionContext _localctx = new PositionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_position);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			match(T__3);
			setState(109);
			number();
			setState(110);
			match(T__4);
			setState(111);
			number();
			setState(112);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public List<TerminalNode> DIGIT() { return getTokens(RelAlgebraParser.DIGIT); }
		public TerminalNode DIGIT(int i) {
			return getToken(RelAlgebraParser.DIGIT, i);
		}
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitNumber(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(114);
				match(DIGIT);
				}
				}
				setState(117); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DIGIT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectionContext extends ParserRuleContext {
		public TerminalNode SELECTION() { return getToken(RelAlgebraParser.SELECTION, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public SelectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterSelection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitSelection(this);
		}
	}

	public final SelectionContext selection() throws RecognitionException {
		SelectionContext _localctx = new SelectionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_selection);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(SELECTION);
			setState(120);
			match(PREDICATE);
			setState(121);
			unary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProjectionContext extends ParserRuleContext {
		public TerminalNode PROJECTION() { return getToken(RelAlgebraParser.PROJECTION, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public ProjectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_projection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterProjection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitProjection(this);
		}
	}

	public final ProjectionContext projection() throws RecognitionException {
		ProjectionContext _localctx = new ProjectionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_projection);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(PROJECTION);
			setState(124);
			match(PREDICATE);
			setState(125);
			unary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SortContext extends ParserRuleContext {
		public TerminalNode SORT() { return getToken(RelAlgebraParser.SORT, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public SortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sort; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterSort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitSort(this);
		}
	}

	public final SortContext sort() throws RecognitionException {
		SortContext _localctx = new SortContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_sort);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(SORT);
			setState(128);
			match(PREDICATE);
			setState(129);
			unary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GroupContext extends ParserRuleContext {
		public TerminalNode GROUP() { return getToken(RelAlgebraParser.GROUP, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public GroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_group; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitGroup(this);
		}
	}

	public final GroupContext group() throws RecognitionException {
		GroupContext _localctx = new GroupContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_group);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(GROUP);
			setState(132);
			match(PREDICATE);
			setState(133);
			unary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RenameContext extends ParserRuleContext {
		public TerminalNode RENAME() { return getToken(RelAlgebraParser.RENAME, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public RenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rename; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterRename(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitRename(this);
		}
	}

	public final RenameContext rename() throws RecognitionException {
		RenameContext _localctx = new RenameContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_rename);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(RENAME);
			setState(136);
			match(PREDICATE);
			setState(137);
			unary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AggregationContext extends ParserRuleContext {
		public TerminalNode AGGREGATION() { return getToken(RelAlgebraParser.AGGREGATION, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public AggregationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterAggregation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitAggregation(this);
		}
	}

	public final AggregationContext aggregation() throws RecognitionException {
		AggregationContext _localctx = new AggregationContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_aggregation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(AGGREGATION);
			setState(140);
			match(PREDICATE);
			setState(141);
			unary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JoinContext extends ParserRuleContext {
		public TerminalNode JOIN() { return getToken(RelAlgebraParser.JOIN, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public BinaryContext binary() {
			return getRuleContext(BinaryContext.class,0);
		}
		public JoinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterJoin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitJoin(this);
		}
	}

	public final JoinContext join() throws RecognitionException {
		JoinContext _localctx = new JoinContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(JOIN);
			setState(144);
			match(PREDICATE);
			setState(145);
			binary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LeftJoinContext extends ParserRuleContext {
		public TerminalNode LEFT_JOIN() { return getToken(RelAlgebraParser.LEFT_JOIN, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public BinaryContext binary() {
			return getRuleContext(BinaryContext.class,0);
		}
		public LeftJoinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leftJoin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterLeftJoin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitLeftJoin(this);
		}
	}

	public final LeftJoinContext leftJoin() throws RecognitionException {
		LeftJoinContext _localctx = new LeftJoinContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_leftJoin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(LEFT_JOIN);
			setState(148);
			match(PREDICATE);
			setState(149);
			binary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RightJoinContext extends ParserRuleContext {
		public TerminalNode RIGHT_JOIN() { return getToken(RelAlgebraParser.RIGHT_JOIN, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public BinaryContext binary() {
			return getRuleContext(BinaryContext.class,0);
		}
		public RightJoinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rightJoin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterRightJoin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitRightJoin(this);
		}
	}

	public final RightJoinContext rightJoin() throws RecognitionException {
		RightJoinContext _localctx = new RightJoinContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_rightJoin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			match(RIGHT_JOIN);
			setState(152);
			match(PREDICATE);
			setState(153);
			binary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CartesianProductContext extends ParserRuleContext {
		public TerminalNode CARTESIAN_PRODUCT() { return getToken(RelAlgebraParser.CARTESIAN_PRODUCT, 0); }
		public BinaryContext binary() {
			return getRuleContext(BinaryContext.class,0);
		}
		public CartesianProductContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cartesianProduct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterCartesianProduct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitCartesianProduct(this);
		}
	}

	public final CartesianProductContext cartesianProduct() throws RecognitionException {
		CartesianProductContext _localctx = new CartesianProductContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_cartesianProduct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(CARTESIAN_PRODUCT);
			setState(156);
			binary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnionContext extends ParserRuleContext {
		public TerminalNode UNION() { return getToken(RelAlgebraParser.UNION, 0); }
		public BinaryContext binary() {
			return getRuleContext(BinaryContext.class,0);
		}
		public UnionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_union; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterUnion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitUnion(this);
		}
	}

	public final UnionContext union() throws RecognitionException {
		UnionContext _localctx = new UnionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_union);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			match(UNION);
			setState(159);
			binary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntersectionContext extends ParserRuleContext {
		public TerminalNode INTERSECTION() { return getToken(RelAlgebraParser.INTERSECTION, 0); }
		public BinaryContext binary() {
			return getRuleContext(BinaryContext.class,0);
		}
		public IntersectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intersection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterIntersection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitIntersection(this);
		}
	}

	public final IntersectionContext intersection() throws RecognitionException {
		IntersectionContext _localctx = new IntersectionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_intersection);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			match(INTERSECTION);
			setState(162);
			binary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryContext extends ParserRuleContext {
		public RelationContext relation() {
			return getRuleContext(RelationContext.class,0);
		}
		public UnaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitUnary(this);
		}
	}

	public final UnaryContext unary() throws RecognitionException {
		UnaryContext _localctx = new UnaryContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_unary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(T__6);
			setState(165);
			relation();
			setState(166);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BinaryContext extends ParserRuleContext {
		public List<RelationContext> relation() {
			return getRuleContexts(RelationContext.class);
		}
		public RelationContext relation(int i) {
			return getRuleContext(RelationContext.class,i);
		}
		public BinaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterBinary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitBinary(this);
		}
	}

	public final BinaryContext binary() throws RecognitionException {
		BinaryContext _localctx = new BinaryContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_binary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(T__6);
			setState(169);
			relation();
			setState(170);
			match(T__4);
			setState(171);
			relation();
			setState(172);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationContext extends ParserRuleContext {
		public RelationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relation; }
	 
		public RelationContext() { }
		public void copyFrom(RelationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SimpleContext extends RelationContext {
		public TerminalNode RELATION() { return getToken(RelAlgebraParser.RELATION, 0); }
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public SimpleContext(RelationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterSimple(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitSimple(this);
		}
	}
	public static class NestedContext extends RelationContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public NestedContext(RelationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterNested(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitNested(this);
		}
	}

	public final RelationContext relation() throws RecognitionException {
		RelationContext _localctx = new RelationContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_relation);
		int _la;
		try {
			setState(179);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RELATION:
				_localctx = new SimpleContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(174);
				match(RELATION);
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__3) {
					{
					setState(175);
					position();
					}
				}

				}
				break;
			case SELECTION:
			case PROJECTION:
			case JOIN:
			case LEFT_JOIN:
			case RIGHT_JOIN:
			case UNION:
			case CARTESIAN_PRODUCT:
			case INTERSECTION:
			case SORT:
			case GROUP:
			case AGGREGATION:
			case RENAME:
				_localctx = new NestedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(178);
				expression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\36\u00b8\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\3\2\3\2\5\2\67\n\2\3\2\3\2\3\2\3\2\3\2\5\2>\n\2\3\2\3\2\7\2B"+
		"\n\2\f\2\16\2E\13\2\3\3\3\3\3\3\5\3J\n\3\3\4\3\4\3\4\3\5\3\5\3\5\5\5R"+
		"\n\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\5\7\\\n\7\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\5\bj\n\b\3\b\5\bm\n\b\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\n\6\nv\n\n\r\n\16\nw\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3"+
		"\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24"+
		"\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\31\3\31\5\31\u00b3\n\31\3\31\5\31\u00b6\n\31\3\31\2\2\32"+
		"\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\2\2\2\u00b8\2\66\3"+
		"\2\2\2\4F\3\2\2\2\6K\3\2\2\2\bQ\3\2\2\2\nU\3\2\2\2\fY\3\2\2\2\16i\3\2"+
		"\2\2\20n\3\2\2\2\22u\3\2\2\2\24y\3\2\2\2\26}\3\2\2\2\30\u0081\3\2\2\2"+
		"\32\u0085\3\2\2\2\34\u0089\3\2\2\2\36\u008d\3\2\2\2 \u0091\3\2\2\2\"\u0095"+
		"\3\2\2\2$\u0099\3\2\2\2&\u009d\3\2\2\2(\u00a0\3\2\2\2*\u00a3\3\2\2\2,"+
		"\u00a6\3\2\2\2.\u00aa\3\2\2\2\60\u00b5\3\2\2\2\62\67\5\4\3\2\63\67\5\16"+
		"\b\2\64\67\5\f\7\2\65\67\5\n\6\2\66\62\3\2\2\2\66\63\3\2\2\2\66\64\3\2"+
		"\2\2\66\65\3\2\2\2\678\3\2\2\28C\7\3\2\29>\5\4\3\2:>\5\16\b\2;>\5\f\7"+
		"\2<>\5\n\6\2=9\3\2\2\2=:\3\2\2\2=;\3\2\2\2=<\3\2\2\2>?\3\2\2\2?@\7\3\2"+
		"\2@B\3\2\2\2A=\3\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2D\3\3\2\2\2EC\3\2"+
		"\2\2FG\7\27\2\2GI\5\b\5\2HJ\5\6\4\2IH\3\2\2\2IJ\3\2\2\2J\5\3\2\2\2KL\7"+
		"\30\2\2LM\7\31\2\2M\7\3\2\2\2NR\7\34\2\2OP\7\35\2\2PR\7\31\2\2QN\3\2\2"+
		"\2QO\3\2\2\2RS\3\2\2\2ST\7\4\2\2T\t\3\2\2\2UV\7\31\2\2VW\7\5\2\2WX\5\16"+
		"\b\2X\13\3\2\2\2Y[\7\31\2\2Z\\\5\20\t\2[Z\3\2\2\2[\\\3\2\2\2\\\r\3\2\2"+
		"\2]j\5\24\13\2^j\5\26\f\2_j\5 \21\2`j\5\"\22\2aj\5$\23\2bj\5&\24\2cj\5"+
		"(\25\2dj\5*\26\2ej\5\30\r\2fj\5\32\16\2gj\5\36\20\2hj\5\34\17\2i]\3\2"+
		"\2\2i^\3\2\2\2i_\3\2\2\2i`\3\2\2\2ia\3\2\2\2ib\3\2\2\2ic\3\2\2\2id\3\2"+
		"\2\2ie\3\2\2\2if\3\2\2\2ig\3\2\2\2ih\3\2\2\2jl\3\2\2\2km\5\20\t\2lk\3"+
		"\2\2\2lm\3\2\2\2m\17\3\2\2\2no\7\6\2\2op\5\22\n\2pq\7\7\2\2qr\5\22\n\2"+
		"rs\7\b\2\2s\21\3\2\2\2tv\7\33\2\2ut\3\2\2\2vw\3\2\2\2wu\3\2\2\2wx\3\2"+
		"\2\2x\23\3\2\2\2yz\7\13\2\2z{\7\32\2\2{|\5,\27\2|\25\3\2\2\2}~\7\f\2\2"+
		"~\177\7\32\2\2\177\u0080\5,\27\2\u0080\27\3\2\2\2\u0081\u0082\7\23\2\2"+
		"\u0082\u0083\7\32\2\2\u0083\u0084\5,\27\2\u0084\31\3\2\2\2\u0085\u0086"+
		"\7\24\2\2\u0086\u0087\7\32\2\2\u0087\u0088\5,\27\2\u0088\33\3\2\2\2\u0089"+
		"\u008a\7\26\2\2\u008a\u008b\7\32\2\2\u008b\u008c\5,\27\2\u008c\35\3\2"+
		"\2\2\u008d\u008e\7\25\2\2\u008e\u008f\7\32\2\2\u008f\u0090\5,\27\2\u0090"+
		"\37\3\2\2\2\u0091\u0092\7\r\2\2\u0092\u0093\7\32\2\2\u0093\u0094\5.\30"+
		"\2\u0094!\3\2\2\2\u0095\u0096\7\16\2\2\u0096\u0097\7\32\2\2\u0097\u0098"+
		"\5.\30\2\u0098#\3\2\2\2\u0099\u009a\7\17\2\2\u009a\u009b\7\32\2\2\u009b"+
		"\u009c\5.\30\2\u009c%\3\2\2\2\u009d\u009e\7\21\2\2\u009e\u009f\5.\30\2"+
		"\u009f\'\3\2\2\2\u00a0\u00a1\7\20\2\2\u00a1\u00a2\5.\30\2\u00a2)\3\2\2"+
		"\2\u00a3\u00a4\7\22\2\2\u00a4\u00a5\5.\30\2\u00a5+\3\2\2\2\u00a6\u00a7"+
		"\7\t\2\2\u00a7\u00a8\5\60\31\2\u00a8\u00a9\7\n\2\2\u00a9-\3\2\2\2\u00aa"+
		"\u00ab\7\t\2\2\u00ab\u00ac\5\60\31\2\u00ac\u00ad\7\7\2\2\u00ad\u00ae\5"+
		"\60\31\2\u00ae\u00af\7\n\2\2\u00af/\3\2\2\2\u00b0\u00b2\7\31\2\2\u00b1"+
		"\u00b3\5\20\t\2\u00b2\u00b1\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b6\3"+
		"\2\2\2\u00b4\u00b6\5\16\b\2\u00b5\u00b0\3\2\2\2\u00b5\u00b4\3\2\2\2\u00b6"+
		"\61\3\2\2\2\r\66=CIQ[ilw\u00b2\u00b5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}