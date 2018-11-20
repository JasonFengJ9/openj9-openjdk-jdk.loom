/*
 * Copyright (c) 2005, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javax.annotation.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Set;
import java.lang.annotation.Annotation;

/**
 * An annotation processing tool framework will {@linkplain
 * Processor#process provide an annotation processor with an object
 * implementing this interface} so that the processor can query for
 * information about a round of annotation processing.
 *
 * @author Joseph D. Darcy
 * @author Scott Seligman
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
public interface RoundEnvironment {
    /**
     * Returns {@code true} if types generated by this round will not
     * be subject to a subsequent round of annotation processing;
     * returns {@code false} otherwise.
     *
     * @return {@code true} if types generated by this round will not
     * be subject to a subsequent round of annotation processing;
     * returns {@code false} otherwise
     */
    boolean processingOver();

    /**
     * Returns {@code true} if an error was raised in the prior round
     * of processing; returns {@code false} otherwise.
     *
     * @return {@code true} if an error was raised in the prior round
     * of processing; returns {@code false} otherwise
     */
    boolean errorRaised();

    /**
     * Returns the {@linkplain Processor root elements} for annotation processing generated
     * by the prior round.
     *
     * @return the root elements for annotation processing generated
     * by the prior round, or an empty set if there were none
     */
    Set<? extends Element> getRootElements();

    /**
     * Returns the elements annotated with the given annotation type.
     * The annotation may appear directly or be inherited.  Only
     * package elements, module elements, and type elements <i>included</i> in this
     * round of annotation processing, or declarations of members,
     * constructors, parameters, or type parameters declared within
     * those, are returned.  Included type elements are {@linkplain
     * #getRootElements root types} and any member types nested within
     * them.  Elements of a package are not considered included simply
     * because a {@code package-info} file for that package was
     * created.
     * Likewise, elements of a module are not considered included
     * simply because a {@code module-info} file for that module was
     * created.
     *
     * @param a  annotation type being requested
     * @return the elements annotated with the given annotation type,
     * or an empty set if there are none
     * @throws IllegalArgumentException if the argument does not
     * represent an annotation type
     */
    Set<? extends Element> getElementsAnnotatedWith(TypeElement a);

    /**
     * Returns the elements annotated with one or more of the given
     * annotation types.
     *
     * @apiNote This method may be useful when processing repeating
     * annotations by looking for an annotation type and its
     * containing annotation type at the same time.
     *
     * @implSpec The default implementation of this method creates an
     * empty result set, iterates over the annotations in the argument
     * array calling {@link #getElementsAnnotatedWith(TypeElement)} on
     * each annotation and adding those results to the result
     * set. Finally, the contents of the result set are returned as an
     * unmodifiable set.
     *
     * @param annotations  annotation types being requested
     * @return the elements annotated with one or more of the given
     * annotation types, or an empty set if there are none
     * @throws IllegalArgumentException if the any elements of the
     * argument set do not represent an annotation type
     * @jls 9.6.3 Repeatable Annotation Types
     * @since 9
     */
    default Set<? extends Element> getElementsAnnotatedWithAny(TypeElement... annotations){
        // Use LinkedHashSet rather than HashSet for predictability
        Set<Element> result = new LinkedHashSet<>();
        for (TypeElement annotation : annotations) {
            result.addAll(getElementsAnnotatedWith(annotation));
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Returns the elements annotated with the given annotation type.
     * The annotation may appear directly or be inherited.  Only
     * package elements, module elements, and type elements <i>included</i> in this
     * round of annotation processing, or declarations of members,
     * constructors, parameters, or type parameters declared within
     * those, are returned.  Included type elements are {@linkplain
     * #getRootElements root types} and any member types nested within
     * them.  Elements in a package are not considered included simply
     * because a {@code package-info} file for that package was
     * created.
     * Likewise, elements of a module are not considered included
     * simply because a {@code module-info} file for that module was
     * created.
     *
     * <p> Note: An implementation of this method typically performs
     * an internal conversion from the runtime reflective
     * representation of an annotation type as a {@code Class} object
     * to a different representation used for annotation
     * processing. The set of annotation types present in the runtime
     * context may differ from the set of annotation types present in
     * the context of annotation processing in a particular
     * environmental configuration. If an runtime annotation type is
     * not present in the annotation processing context, the situation
     * is not treated as an error and no elements are found for that
     * annotation type.
     *
     * @param a  annotation type being requested
     * @return the elements annotated with the given annotation type,
     * or an empty set if there are none
     * @throws IllegalArgumentException if the argument does not
     * represent an annotation type
     *
     * @see javax.lang.model.AnnotatedConstruct#getAnnotation(Class)
     * @see javax.lang.model.AnnotatedConstruct#getAnnotationsByType(Class)
     */
    Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> a);

    /**
     * Returns the elements annotated with one or more of the given
     * annotation types.
     *
     * <p> Note: An implementation of this method typically performs
     * an internal conversion from the runtime reflective
     * representation of an annotation type as a {@code Class} object
     * to a different representation used for annotation
     * processing. The set of annotation types present in the runtime
     * context may differ from the set of annotation types present in
     * the context of annotation processing in a particular
     * environmental configuration. If an runtime annotation type is
     * not present in the annotation processing context, the situation
     * is not treated as an error and no elements are found for that
     * annotation type.
     *
     * @apiNote This method may be useful when processing repeating
     * annotations by looking for an annotation type and its
     * containing annotation type at the same time.
     *
     * @implSpec The default implementation of this method creates an
     * empty result set, iterates over the annotations in the argument
     * set calling {@link #getElementsAnnotatedWith(Class)} on
     * each annotation and adding those results to the result
     * set. Finally, the contents of the result set are returned as an
     * unmodifiable set.
     *
     * @param annotations  annotation types being requested
     * @return the elements annotated with one or more of the given
     * annotation types, or an empty set if there are none
     * @throws IllegalArgumentException if the any elements of the
     * argument set do not represent an annotation type
     * @jls 9.6.3 Repeatable Annotation Types
     *
     * @see javax.lang.model.AnnotatedConstruct#getAnnotation(Class)
     * @see javax.lang.model.AnnotatedConstruct#getAnnotationsByType(Class)
     *
     * @since 9
     */
    default Set<? extends Element> getElementsAnnotatedWithAny(Set<Class<? extends Annotation>> annotations){
        // Use LinkedHashSet rather than HashSet for predictability
        Set<Element> result = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : annotations) {
            result.addAll(getElementsAnnotatedWith(annotation));
        }
        return Collections.unmodifiableSet(result);
    }
}
