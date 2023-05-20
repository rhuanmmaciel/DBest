package dsl.antlr4;

// Generated from RelAlgebra.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RelAlgebraParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, SELECTION=7, PROJECTION=8, 
		JOIN=9, LEFTJOIN=10, RIGHTJOIN=11, UNION=12, CARTESIANPRODUCT=13, ATTRIBUTE=14, 
		PREDICATE=15, RELATION=16, DIGIT=17, WS=18;
	public static final int
		RULE_expressions = 0, RULE_expression = 1, RULE_position = 2, RULE_number = 3, 
		RULE_selection = 4, RULE_projection = 5, RULE_join = 6, RULE_leftJoin = 7, 
		RULE_rightJoin = 8, RULE_cartesianProduct = 9, RULE_union = 10, RULE_relation = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"expressions", "expression", "position", "number", "selection", "projection", 
			"join", "leftJoin", "rightJoin", "cartesianProduct", "union", "relation"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'<'", "','", "'>'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "SELECTION", "PROJECTION", 
			"JOIN", "LEFTJOIN", "RIGHTJOIN", "UNION", "CARTESIANPRODUCT", "ATTRIBUTE", 
			"PREDICATE", "RELATION", "DIGIT", "WS"
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
	public static class ExpressionsContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpressionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).enterExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RelAlgebraListener ) ((RelAlgebraListener)listener).exitExpressions(this);
		}
	}

	public final ExpressionsContext expressions() throws RecognitionException {
		ExpressionsContext _localctx = new ExpressionsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expressions);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			expression();
			setState(29);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(25);
					match(T__0);
					setState(26);
					expression();
					}
					} 
				}
				setState(31);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(32);
			match(T__0);
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
		enterRule(_localctx, 2, RULE_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECTION:
				{
				setState(34);
				selection();
				}
				break;
			case PROJECTION:
				{
				setState(35);
				projection();
				}
				break;
			case JOIN:
				{
				setState(36);
				join();
				}
				break;
			case LEFTJOIN:
				{
				setState(37);
				leftJoin();
				}
				break;
			case RIGHTJOIN:
				{
				setState(38);
				rightJoin();
				}
				break;
			case CARTESIANPRODUCT:
				{
				setState(39);
				cartesianProduct();
				}
				break;
			case UNION:
				{
				setState(40);
				union();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(43);
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
		enterRule(_localctx, 4, RULE_position);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(T__1);
			setState(47);
			number();
			setState(48);
			match(T__2);
			setState(49);
			number();
			setState(50);
			match(T__3);
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
		enterRule(_localctx, 6, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(52);
				match(DIGIT);
				}
				}
				setState(55); 
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
		public RelationContext relation() {
			return getRuleContext(RelationContext.class,0);
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
		enterRule(_localctx, 8, RULE_selection);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			match(SELECTION);
			setState(58);
			match(PREDICATE);
			setState(59);
			match(T__4);
			setState(60);
			relation();
			setState(61);
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

	public static class ProjectionContext extends ParserRuleContext {
		public TerminalNode PROJECTION() { return getToken(RelAlgebraParser.PROJECTION, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public RelationContext relation() {
			return getRuleContext(RelationContext.class,0);
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
		enterRule(_localctx, 10, RULE_projection);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(PROJECTION);
			setState(64);
			match(PREDICATE);
			setState(65);
			match(T__4);
			setState(66);
			relation();
			setState(67);
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

	public static class JoinContext extends ParserRuleContext {
		public TerminalNode JOIN() { return getToken(RelAlgebraParser.JOIN, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public List<RelationContext> relation() {
			return getRuleContexts(RelationContext.class);
		}
		public RelationContext relation(int i) {
			return getRuleContext(RelationContext.class,i);
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
		enterRule(_localctx, 12, RULE_join);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(JOIN);
			setState(70);
			match(PREDICATE);
			setState(71);
			match(T__4);
			setState(72);
			relation();
			setState(73);
			match(T__2);
			setState(74);
			relation();
			setState(75);
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

	public static class LeftJoinContext extends ParserRuleContext {
		public TerminalNode LEFTJOIN() { return getToken(RelAlgebraParser.LEFTJOIN, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public List<RelationContext> relation() {
			return getRuleContexts(RelationContext.class);
		}
		public RelationContext relation(int i) {
			return getRuleContext(RelationContext.class,i);
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
		enterRule(_localctx, 14, RULE_leftJoin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77);
			match(LEFTJOIN);
			setState(78);
			match(PREDICATE);
			setState(79);
			match(T__4);
			setState(80);
			relation();
			setState(81);
			match(T__2);
			setState(82);
			relation();
			setState(83);
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

	public static class RightJoinContext extends ParserRuleContext {
		public TerminalNode RIGHTJOIN() { return getToken(RelAlgebraParser.RIGHTJOIN, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public List<RelationContext> relation() {
			return getRuleContexts(RelationContext.class);
		}
		public RelationContext relation(int i) {
			return getRuleContext(RelationContext.class,i);
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
		enterRule(_localctx, 16, RULE_rightJoin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(RIGHTJOIN);
			setState(86);
			match(PREDICATE);
			setState(87);
			match(T__4);
			setState(88);
			relation();
			setState(89);
			match(T__2);
			setState(90);
			relation();
			setState(91);
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

	public static class CartesianProductContext extends ParserRuleContext {
		public TerminalNode CARTESIANPRODUCT() { return getToken(RelAlgebraParser.CARTESIANPRODUCT, 0); }
		public List<RelationContext> relation() {
			return getRuleContexts(RelationContext.class);
		}
		public RelationContext relation(int i) {
			return getRuleContext(RelationContext.class,i);
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
		enterRule(_localctx, 18, RULE_cartesianProduct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(CARTESIANPRODUCT);
			setState(94);
			match(T__4);
			setState(95);
			relation();
			setState(96);
			match(T__2);
			setState(97);
			relation();
			setState(98);
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

	public static class UnionContext extends ParserRuleContext {
		public TerminalNode UNION() { return getToken(RelAlgebraParser.UNION, 0); }
		public TerminalNode PREDICATE() { return getToken(RelAlgebraParser.PREDICATE, 0); }
		public List<RelationContext> relation() {
			return getRuleContexts(RelationContext.class);
		}
		public RelationContext relation(int i) {
			return getRuleContext(RelationContext.class,i);
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
		enterRule(_localctx, 20, RULE_union);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			match(UNION);
			setState(101);
			match(PREDICATE);
			setState(102);
			match(T__4);
			setState(103);
			relation();
			setState(104);
			match(T__2);
			setState(105);
			relation();
			setState(106);
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
		enterRule(_localctx, 22, RULE_relation);
		int _la;
		try {
			setState(113);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RELATION:
				_localctx = new SimpleContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(108);
				match(RELATION);
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__1) {
					{
					setState(109);
					position();
					}
				}

				}
				break;
			case SELECTION:
			case PROJECTION:
			case JOIN:
			case LEFTJOIN:
			case RIGHTJOIN:
			case UNION:
			case CARTESIANPRODUCT:
				_localctx = new NestedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\24v\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\3\2\3\2\3\2\7\2\36\n\2\f\2\16\2!\13\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\5\3,\n\3\3\3\5\3/\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\6"+
		"\58\n\5\r\5\16\59\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\5\rq\n\r\3\r\5\rt\n\r\3\r\2\2\16\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\2\2\2t\2\32\3\2\2\2\4+\3\2\2\2\6\60\3\2\2\2\b\67"+
		"\3\2\2\2\n;\3\2\2\2\fA\3\2\2\2\16G\3\2\2\2\20O\3\2\2\2\22W\3\2\2\2\24"+
		"_\3\2\2\2\26f\3\2\2\2\30s\3\2\2\2\32\37\5\4\3\2\33\34\7\3\2\2\34\36\5"+
		"\4\3\2\35\33\3\2\2\2\36!\3\2\2\2\37\35\3\2\2\2\37 \3\2\2\2 \"\3\2\2\2"+
		"!\37\3\2\2\2\"#\7\3\2\2#\3\3\2\2\2$,\5\n\6\2%,\5\f\7\2&,\5\16\b\2\',\5"+
		"\20\t\2(,\5\22\n\2),\5\24\13\2*,\5\26\f\2+$\3\2\2\2+%\3\2\2\2+&\3\2\2"+
		"\2+\'\3\2\2\2+(\3\2\2\2+)\3\2\2\2+*\3\2\2\2,.\3\2\2\2-/\5\6\4\2.-\3\2"+
		"\2\2./\3\2\2\2/\5\3\2\2\2\60\61\7\4\2\2\61\62\5\b\5\2\62\63\7\5\2\2\63"+
		"\64\5\b\5\2\64\65\7\6\2\2\65\7\3\2\2\2\668\7\23\2\2\67\66\3\2\2\289\3"+
		"\2\2\29\67\3\2\2\29:\3\2\2\2:\t\3\2\2\2;<\7\t\2\2<=\7\21\2\2=>\7\7\2\2"+
		">?\5\30\r\2?@\7\b\2\2@\13\3\2\2\2AB\7\n\2\2BC\7\21\2\2CD\7\7\2\2DE\5\30"+
		"\r\2EF\7\b\2\2F\r\3\2\2\2GH\7\13\2\2HI\7\21\2\2IJ\7\7\2\2JK\5\30\r\2K"+
		"L\7\5\2\2LM\5\30\r\2MN\7\b\2\2N\17\3\2\2\2OP\7\f\2\2PQ\7\21\2\2QR\7\7"+
		"\2\2RS\5\30\r\2ST\7\5\2\2TU\5\30\r\2UV\7\b\2\2V\21\3\2\2\2WX\7\r\2\2X"+
		"Y\7\21\2\2YZ\7\7\2\2Z[\5\30\r\2[\\\7\5\2\2\\]\5\30\r\2]^\7\b\2\2^\23\3"+
		"\2\2\2_`\7\17\2\2`a\7\7\2\2ab\5\30\r\2bc\7\5\2\2cd\5\30\r\2de\7\b\2\2"+
		"e\25\3\2\2\2fg\7\16\2\2gh\7\21\2\2hi\7\7\2\2ij\5\30\r\2jk\7\5\2\2kl\5"+
		"\30\r\2lm\7\b\2\2m\27\3\2\2\2np\7\22\2\2oq\5\6\4\2po\3\2\2\2pq\3\2\2\2"+
		"qt\3\2\2\2rt\5\4\3\2sn\3\2\2\2sr\3\2\2\2t\31\3\2\2\2\b\37+.9ps";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}