
package edu.wustl.cab2b.server.path.pathgen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An adjacency list representation of a (directed) graph. Each vertex of the
 * graph is a {@link edu.wustl.cab2b.server.path.pathgen.Node}.
 * @see edu.wustl.cab2b.server.path.pathgen.Node
 * @author srinath_k
 */
public class Graph implements Cloneable
{

	/**
     * The graph nodes.
     */
	private transient Map<Node, Set<Node>> graphNodes;

	/**
	 * Default constructor for Graph.
	 */
	public Graph()
	{
		graphNodes = new HashMap<Node, Set<Node>>();
	}

	/**
	 * Creates the graph from an adjacency matrix. The {@link Node}s have id's
	 * corresponding to the row/column index in the matrix.
	 * @param adjacencyMatrix
	 *            adjacency matrix representation of a graph; (i,j) = true if
	 *            and only if a directed edge i -> j is present.
	 * @throws IllegalArgumentException
	 *             if the adjacency matrix is null, or if it is not a square
	 *             matrix.
	 */
	public Graph(boolean[][] adjacencyMatrix)
	{
		if (adjacencyMatrix == null)
		{
			throw new IllegalArgumentException("Adjacency matrix cannot be null.");
		}
		if (adjacencyMatrix.length == 0)
		{
			graphNodes = new HashMap<Node, Set<Node>>();
			return;
		}
		if (adjacencyMatrix.length != adjacencyMatrix[0].length)
		{
			throw new IllegalArgumentException("Adjacency matrix must be square.");
		}
		graphNodes = new HashMap<Node, Set<Node>>();
		addNodesToGraph(adjacencyMatrix);
	}


    /**
     * Adds the nodes to graph.
     *
     * @param adjacencyMatrix
     *            the adjacency matrix
     */
    private void addNodesToGraph(boolean[][] adjacencyMatrix)
    {
        Node[] nodes = new Node[adjacencyMatrix.length];
		for (int i = 0; i < adjacencyMatrix.length; i++)
		{
			nodes[i] = getNode(i);
			addNode(nodes[i]);
		}
		for (int i = 0; i < adjacencyMatrix.length; i++)
		{
			for (int j = 0; j < adjacencyMatrix.length; j++)
			{
				if (adjacencyMatrix[i][j])
				{
					addAdjacentNode(nodes[i], nodes[j]);
				}
			}
		}
    }

	/**
     * Gets the node.
     *
     * @param index
     *            the index
     * @return the node
     */
	private Node getNode(int index)
    {
        return new Node(index);
    }

    /**
     * Adds adjacent nodes.
     *
     * @param fromNode
     *            the from node
     * @param toNode
     *            the to node
     */
	public final void addAdjacentNode(Node fromNode, Node toNode)
	{
		if (!containsNode(fromNode))
		{
			addNode(fromNode);
		}
		getAdjacentNodes(fromNode).add(toNode);
	}

	/**
     * Adds new Node.
     *
     * @param node
     *            the node
     */
	public final void addNode(Node node)
	{
		graphNodes.put(node, new HashSet<Node>());
	}

	/**
     * Removes a Node from Graph.
     *
     * @param node
     *            the node
     * @return true, if successful
     */
	public boolean removeNode(Node node)
	{
	    boolean isNodeRemoved = true; // NOPMD by gaurav_sawant on 5/6/10 11:16 AM
		if (!containsNode(node))
		{
		    isNodeRemoved = false;
		}
		graphNodes.remove(node);
		for (Node fromNode : graphNodes.keySet())
		{
			removeEdge(fromNode, node);
		}
		return isNodeRemoved;
	}

	/**
     * Removes edge from Graph.
     *
     * @param fromNode
     *            the from node
     * @param toNode
     *            the to node
     * @return true, if successful
     */
	public boolean removeEdge(Node fromNode, Node toNode)
	{
	    boolean isEdgeRemoved = true; // NOPMD by gaurav_sawant on 5/6/10 11:16 AM
		if (!containsNode(fromNode))
		{
		    isEdgeRemoved = false; // NOPMD by gaurav_sawant on 5/6/10 11:16 AM
		}
		isEdgeRemoved = getAdjacentNodes(fromNode).remove(toNode);
		return isEdgeRemoved;
	}

	/**
     * Node y is adjacent to node x iff edge (x,y) exists.
     *
     * @param node
     *            the node
     * @return set of adjacent nodes.
     */
	public Set<Node> getAdjacentNodes(Node node)
	{
		return graphNodes.get(node);
	}

	/**
     * Check whether Node is in Graph or not.
     *
     * @param node
     *            the node
     * @return true, if successful
     */
	public final boolean containsNode(Node node)
	{
		return graphNodes.containsKey(node);
	}

	/**
     * Overrides Object clone method.
     *
     * @return the graph
     * @see java.lang.Object#clone()
     */
	public Graph clone()
	{
		Graph clone = new Graph();

		for (Map.Entry<Node, Set<Node>> entry : graphNodes.entrySet())
		{
			Node fromNode = entry.getKey();
			clone.addNode(fromNode);
			for (Node toNode : entry.getValue())
			{
				clone.addAdjacentNode(fromNode, toNode);
			}
		}
		return clone;
	}

	/**
     * Returns no. of node in Graph.
     *
     * @return no. of node in Graph
     */
	public int numberOfNodes()
	{
		return graphNodes.size();
	}

	/**
     * Returns all nodes.
     *
     * @return the set
     */
	public Set<Node> allNodes()
	{
		return graphNodes.keySet();
	}

	/**
     * Clears Graph.
     */
	public void clear()
	{
		graphNodes.clear();
	}

	/**
     * Checks whether edge present in Graph or not.
     *
     * @param fromNode
     *            the from node
     * @param toNode
     *            the to node
     * @return true, if is edge present
     */
	public boolean isEdgePresent(Node fromNode, Node toNode)
	{
	    boolean isPresent = true; // NOPMD by gaurav_sawant on 5/6/10 11:16 AM
		if (!containsNode(fromNode))
		{
		    isPresent = false; // NOPMD by gaurav_sawant on 5/6/10 11:17 AM
		}
		isPresent = getAdjacentNodes(fromNode).contains(toNode);
		return isPresent;
	}

	/**
     * Overrides Object equals. Returns
     *
     * @param obj
     *            the obj
     * @return true if this object is equal to obj
     * @see java.lang.Object#equals(Object)
     */
	@Override
	public boolean equals(Object obj)
	{
	    boolean isEqual = true; // NOPMD by gaurav_sawant on 5/6/10 11:17 AM
		if (this == obj)
		{
		    isEqual = true; // NOPMD by gaurav_sawant on 5/6/10 11:17 AM
		}
		if (!(obj instanceof Graph))
		{
		    isEqual = false; // NOPMD by gaurav_sawant on 5/6/10 11:17 AM
		}
		Graph otherGraph = (Graph) obj;
		isEqual = graphNodes.equals(otherGraph.graphNodes);
		return isEqual;
	}

	/**
     * Hash code.
     *
     * @return hash code
     * @see java.lang.Object#hashCode()
     */
	public int hashCode()
	{
		return graphNodes.hashCode();
	}
}
