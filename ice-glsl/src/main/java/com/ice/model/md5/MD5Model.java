package com.ice.model.md5;

import android.graphics.PointF;
import android.opengl.Matrix;
import com.ice.model.Vec3;

import java.nio.Buffer;
import java.util.List;

/**
 * Author: donnyliu
 */
public class MD5Model
{

	public static class Vertex
	{
		Vec3   m_Pos;
		Vec3   m_Normal;
		PointF m_Tex0;
		int    m_StartWeight;
		int    m_WeightCount;
	}

	public static class Triangle
	{
		int m_Indices[];//3
	}

	public static class Weight
	{
		int   m_JointID;
		float m_Bias;
		Vec3  m_Pos;
	}


	public static class Joint
	{
		String m_Name;
		int    m_ParentID;
		Vec3   m_Pos;
		Vec3   m_Orient;  //glm::quat
	}

	public static class Mesh
	{
		String         m_Shader;
		// This vertex list stores the vertices in the bind pose.
		List<Vertex>   m_Verts;
		List<Triangle> m_Tris;
		List<Weight>   m_Weights;

		// A texture ID for the material
		int m_TexID;

		// These buffers are used for rendering the animated mesh
		Buffer m_PositionBuffer;   // Vertex position stream
		Buffer m_NormalBuffer;     // Vertex normals stream
		Buffer m_Tex2DBuffer;      // Texture coordinate set
		Buffer m_IndexBuffer;      // Vertex index buffer
	}

	int m_iMD5Version;
	int m_iNumJoints;
	int m_iNumMeshes;

	boolean m_bHasAnimation;

	List<Joint> m_Joints;
	List<Mesh>  m_Meshes;

	MD5Animation m_Animation;

	Matrix m_LocalToWorldMatrix;

	public boolean LoadModel (String filename)
	{
		return true;
	}

	public boolean LoadAnim (String filename)
	{
		return true;
	}

	public void Update (float fDeltaTime)
	{
	}

	public void Render ()
	{
	}

	//	typedef std::vector<glm::vec3> PositionBuffer;
	//	typedef std::vector<glm::vec3> NormalBuffer;
	//	typedef std::vector<glm::vec2> Tex2DBuffer;
	//	typedef std::vector<GLuint> IndexBuffer;

	// Prepare the mesh for rendering
	// Compute vertex positions and normals
	boolean PrepareMesh (Mesh mesh)
	{
		return true;
	}

	boolean PrepareMesh (Mesh mesh, MD5Animation.FrameSkeleton skel)
	{
		return true;
	}

	boolean PrepareNormals (Mesh mesh)
	{
		return true;
	}

	// Render a single mesh of the model
	void RenderMesh (Mesh mesh)
	{
	}

	void RenderNormals (Mesh mesh)
	{

	}

	// Draw the skeleton of the mesh for debugging purposes.
	void RenderSkeleton (List<Joint> joints)
	{

	}

	boolean CheckAnimation (MD5Animation animation)
	{
		return true;
	}

}
