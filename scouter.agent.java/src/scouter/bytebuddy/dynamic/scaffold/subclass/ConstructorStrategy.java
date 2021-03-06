// Generated by delombok at Sun Feb 26 12:31:38 KST 2017
package scouter.bytebuddy.dynamic.scaffold.subclass;

import scouter.bytebuddy.description.method.MethodDescription;
import scouter.bytebuddy.description.method.MethodList;
import scouter.bytebuddy.description.type.TypeDescription;
import scouter.bytebuddy.dynamic.Transformer;
import scouter.bytebuddy.dynamic.scaffold.MethodRegistry;
import scouter.bytebuddy.implementation.SuperMethodCall;
import scouter.bytebuddy.implementation.attribute.MethodAttributeAppender;
import scouter.bytebuddy.matcher.LatentMatcher;
import scouter.bytebuddy.jar.asm.Opcodes;
import scouter.bytebuddy.matcher.ElementMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static scouter.bytebuddy.matcher.ElementMatchers.*;

/**
 * A constructor strategy is responsible for creating bootstrap constructors for a
 * {@link SubclassDynamicTypeBuilder}.
 *
 * @see ConstructorStrategy.Default
 */
public interface ConstructorStrategy {
    /**
     * Extracts constructors for a given super type. The extracted constructor signatures will then be imitated by the
     * created dynamic type.
     *
     * @param instrumentedType The type for which the constructors should be created.
     * @return A list of tokens that describe the constructors that are to be implemented.
     */
    List<MethodDescription.Token> extractConstructors(TypeDescription instrumentedType);

    /**
     * Returns a method registry that is capable of creating byte code for the constructors that were
     * provided by the
     * {@link ConstructorStrategy#extractConstructors(TypeDescription)}
     * method of this instance.
     *
     * @param methodRegistry The original method registry.
     * @return A method registry that is capable of providing byte code for the constructors that were added by this strategy.
     */
    MethodRegistry inject(MethodRegistry methodRegistry);


    /**
     * Default implementations of constructor strategies. Any such strategy offers to additionally apply an {@link MethodAttributeAppender.Factory}.
     */
    enum Default implements ConstructorStrategy {
        /**
         * This strategy is adding no constructors such that the instrumented type will by default not have any. This
         * is legal by Java byte code requirements. However, if no constructor is added manually if this strategy is
         * applied, the type is not constructable without using JVM non-public functionality.
         */
        NO_CONSTRUCTORS {
            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription superClass) {
                return Collections.emptyList();
            }

            @Override
            protected MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry;
            }
        },
        
        /**
         * This strategy is adding a default constructor that calls it's super types default constructor. If no such
         * constructor is defined by the super class, an {@link IllegalArgumentException} is thrown. Note that the default
         * constructor needs to be visible to its sub type for this strategy to work. The declared default constructor of
         * the created class is declared public and without annotations.
         */
        DEFAULT_CONSTRUCTOR {
            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                MethodList<?> defaultConstructors = superClass == null ? new MethodList.Empty<MethodDescription.InGenericShape>() : superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(0)).<MethodDescription>and(ElementMatchers.isVisibleTo(instrumentedType)));
                if (defaultConstructors.size() == 1) {
                    return Collections.singletonList(new MethodDescription.Token(Opcodes.ACC_PUBLIC));
                } else {
                    throw new IllegalArgumentException(instrumentedType.getSuperClass() + " declares no constructor that is visible to " + instrumentedType);
                }
            }

            @Override
            protected MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry.append(new LatentMatcher.Resolved<MethodDescription>(ElementMatchers.isConstructor()), new MethodRegistry.Handler.ForImplementation(SuperMethodCall.INSTANCE), methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
            }
        },
        
        /**
         * This strategy is adding all constructors of the instrumented type's super class where each constructor is
         * directly invoking its signature-equivalent super class constructor. Only constructors that are visible to the
         * instrumented type are added, i.e. package-private constructors are only added if the super type is defined
         * in the same package as the instrumented type and private constructors are always skipped.
         */
        IMITATE_SUPER_CLASS {
            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                return (superClass == null ? new MethodList.Empty<MethodDescription.InGenericShape>() : superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.isVisibleTo(instrumentedType)))).asTokenList(is(instrumentedType));
            }

            @Override
            public MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry.append(new LatentMatcher.Resolved<MethodDescription>(ElementMatchers.isConstructor()), new MethodRegistry.Handler.ForImplementation(SuperMethodCall.INSTANCE), methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
            }
        },
        
        /**
         * This strategy is adding all constructors of the instrumented type's super class where each constructor is
         * directly invoking its signature-equivalent super class constructor. Only {@code public} constructors are
         * added.
         */
        IMITATE_SUPER_CLASS_PUBLIC {
            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                return (superClass == null ? new MethodList.Empty<MethodDescription.InGenericShape>() : superClass.getDeclaredMethods().filter(ElementMatchers.isPublic().and(ElementMatchers.isConstructor()))).asTokenList(is(instrumentedType));
            }

            @Override
            public MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry.append(new LatentMatcher.Resolved<MethodDescription>(ElementMatchers.isConstructor()), new MethodRegistry.Handler.ForImplementation(SuperMethodCall.INSTANCE), methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
            }
        },
        
        /**
         * This strategy is adding all constructors of the instrumented type's super class where each constructor is
         * directly invoking its signature-equivalent super class constructor. A constructor is added for any constructor
         * of the super class that is invokable and is declared as {@code public}.
         */
        IMITATE_SUPER_CLASS_OPENING {
            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                return (superClass == null ? new MethodList.Empty<MethodDescription.InGenericShape>() : superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.isVisibleTo(instrumentedType)))).asTokenList(is(instrumentedType));
            }

            @Override
            public MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry.append(new LatentMatcher.Resolved<MethodDescription>(ElementMatchers.isConstructor()), new MethodRegistry.Handler.ForImplementation(SuperMethodCall.INSTANCE), methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
            }

            @Override
            protected int resolveModifier(int modifiers) {
                return Opcodes.ACC_PUBLIC;
            }
        };

        @Override
        public List<MethodDescription.Token> extractConstructors(TypeDescription instrumentedType) {
            List<MethodDescription.Token> tokens = doExtractConstructors(instrumentedType);
            List<MethodDescription.Token> stripped = new ArrayList<MethodDescription.Token>(tokens.size());
            for (MethodDescription.Token token : tokens) {
                stripped.add(new MethodDescription.Token(token.getName(), resolveModifier(token.getModifiers()), token.getTypeVariableTokens(), token.getReturnType(), token.getParameterTokens(), token.getExceptionTypes(), token.getAnnotations(), token.getDefaultValue(), TypeDescription.Generic.UNDEFINED));
            }
            return stripped;
        }

        /**
         * Resolves a constructor's modifiers.
         *
         * @param modifiers The actual constructor's modifiers.
         * @return The resolved modifiers.
         */
        protected int resolveModifier(int modifiers) {
            return modifiers;
        }

        /**
         * Extracts the relevant method tokens of the instrumented type's constructors.
         *
         * @param instrumentedType The type for which to extract the constructors.
         * @return A list of relevant method tokens.
         */
        protected abstract List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType);

        @Override
        public MethodRegistry inject(MethodRegistry methodRegistry) {
            return doInject(methodRegistry, MethodAttributeAppender.NoOp.INSTANCE);
        }

        /**
         * Applies the actual injection with a method attribute appender factory supplied.
         *
         * @param methodRegistry                 The method registry into which to inject the constructors.
         * @param methodAttributeAppenderFactory The method attribute appender to use.
         * @return The resulting method registry.
         */
        protected abstract MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory);

        /**
         * Returns a constructor strategy that supplies the supplied method attribute appender factory.
         *
         * @param methodAttributeAppenderFactory The method attribute appender factory to use.
         * @return A copy of this constructor strategy with the method attribute appender factory applied.
         */
        public ConstructorStrategy with(MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
            return new WithMethodAttributeAppenderFactory(this, methodAttributeAppenderFactory);
        }

        /**
         * Applies this constructor strategy while retaining any of the base constructor's annotations.
         *
         * @return A copy of this constructor strategy which retains any of the base constructor's annotations.
         */
        public ConstructorStrategy withInheritedAnnotations() {
            return new WithMethodAttributeAppenderFactory(this, MethodAttributeAppender.ForInstrumentedMethod.EXCLUDING_RECEIVER);
        }

        /**
         * A wrapper for a default constructor strategy which additionally applies a method attribute appender factory.
         */
        protected static class WithMethodAttributeAppenderFactory implements ConstructorStrategy {
            /**
             * The delegate default constructor strategy.
             */
            private final Default delegate;
            /**
             * The method attribute appender factory to apply.
             */
            private final MethodAttributeAppender.Factory methodAttributeAppenderFactory;

            /**
             * Creates a new wrapper for a default constructor strategy.
             *
             * @param delegate                       The delegate default constructor strategy.
             * @param methodAttributeAppenderFactory The method attribute appender factory to apply.
             */
            protected WithMethodAttributeAppenderFactory(Default delegate, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                this.delegate = delegate;
                this.methodAttributeAppenderFactory = methodAttributeAppenderFactory;
            }

            @Override
            public List<MethodDescription.Token> extractConstructors(TypeDescription instrumentedType) {
                return delegate.extractConstructors(instrumentedType);
            }

            @Override
            public MethodRegistry inject(MethodRegistry methodRegistry) {
                return delegate.doInject(methodRegistry, methodAttributeAppenderFactory);
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            public boolean equals(final java.lang.Object o) {
                if (o == this) return true;
                if (!(o instanceof ConstructorStrategy.Default.WithMethodAttributeAppenderFactory)) return false;
                final ConstructorStrategy.Default.WithMethodAttributeAppenderFactory other = (ConstructorStrategy.Default.WithMethodAttributeAppenderFactory) o;
                if (!other.canEqual((java.lang.Object) this)) return false;
                final java.lang.Object this$delegate = this.delegate;
                final java.lang.Object other$delegate = other.delegate;
                if (this$delegate == null ? other$delegate != null : !this$delegate.equals(other$delegate)) return false;
                final java.lang.Object this$methodAttributeAppenderFactory = this.methodAttributeAppenderFactory;
                final java.lang.Object other$methodAttributeAppenderFactory = other.methodAttributeAppenderFactory;
                if (this$methodAttributeAppenderFactory == null ? other$methodAttributeAppenderFactory != null : !this$methodAttributeAppenderFactory.equals(other$methodAttributeAppenderFactory)) return false;
                return true;
            }

            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            protected boolean canEqual(final java.lang.Object other) {
                return other instanceof ConstructorStrategy.Default.WithMethodAttributeAppenderFactory;
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            @javax.annotation.Generated("lombok")
            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final java.lang.Object $delegate = this.delegate;
                result = result * PRIME + ($delegate == null ? 43 : $delegate.hashCode());
                final java.lang.Object $methodAttributeAppenderFactory = this.methodAttributeAppenderFactory;
                result = result * PRIME + ($methodAttributeAppenderFactory == null ? 43 : $methodAttributeAppenderFactory.hashCode());
                return result;
            }
        }
    }
}
