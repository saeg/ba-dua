/**
 * Copyright (c) 2014 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.internal.instr.df;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.LineNumberNode;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.BytecodeInstructionType;
import br.usp.each.saeg.bytecode.analysis.domain.Edge;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNode;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNodeVisitor;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.VariableRef;
import br.usp.each.saeg.opal.Block;
import br.usp.each.saeg.opal.Graph;

public class GraphBlockCreator extends GraphNodeVisitor {

    private final Graph<Block> graph;
    private final List<VariableRef> vars;

    private Set<Edge> edges;
    private Map<Integer, Integer> gotos;

    public GraphBlockCreator(final Graph<Block> graph, final List<VariableRef> vars) {
        this.graph = graph;
        this.vars = vars;
    }

    @Override
    public void start(final GraphNode root) {
        edges = new LinkedHashSet<Edge>();
        gotos = new HashMap<Integer, Integer>();
    }

    @Override
    public void visit(final GraphNode node) {
        if (node.isGotoNode()) {
            gotos.put(node.id, -1);
            return;
        }
        final Block block = new Block(node.id);
        for (final BytecodeInstruction insn : node.instructions) {
            if (insn.frame != null) {
                if (insn.frame.def != null) {
                    block.def(vars.indexOf(insn.frame.def));
                }
                if (!insn.frame.uses.isEmpty()) {
                    for (final VariableRef var : insn.frame.uses) {
                        if (insn.isPredicate()) {
                            block.puse(vars.indexOf(var));
                        } else {
                            block.cuse(vars.indexOf(var));
                        }
                    }
                }
            }
            if (insn.getType() == BytecodeInstructionType.LINE_NUMBER) {
                final LineNumberNode line = (LineNumberNode) insn.getInstruction();
                block.line(line.line);
            }
        }
        graph.add(block);
    }

    @Override
    public void visitEdge(final GraphNode src, final GraphNode dest) {
        final Integer id = gotos.get(src.id);
        if (id == null) {
            edges.add(new Edge(src.id, dest.id));
        } else if (id == -1) {
            gotos.put(src.id, dest.id);
        }
    }

    @Override
    public void end(final GraphNode root) {
        for (final Edge edge : edges) {
            Integer id = gotos.get(edge.dest);
            if (id == null) {
                graph.addEdge(edge.src, edge.dest);
            } else {
                while (gotos.containsKey(id)) {
                    id = gotos.get(id);
                }
                graph.addEdge(edge.src, id);
            }
        }
        edges = null;
        gotos = null;
    }

}
