package com.ice.model.md5;

import com.ice.model.Vec3;

import java.util.List;

/**
 * Author: donnyliu
 */
public class MD5Animation
{

	// The JointInfo stores the information necessary to build the
	// skeletons for each frame
	public static class JointInfo

	{
		String m_Name;
		int    m_ParentID;
		int    m_Flags;
		int    m_StartIndex;
	}

	public static class Bound

	{
		Vec3 m_Min;
		Vec3 m_Max;
	}

	public static class BaseFrame

	{
		Vec3 m_Pos;
		Vec3 m_Orient; //glm::quat
	}

	public static class FrameData
	{
		int         m_iFrameID;
		List<Float> m_FrameData;
	}

	// A Skeleton joint is a joint of the skeleton per frame
	public static class SkeletonJoint
	{
		int m_Parent = -1;
		Vec3 m_Pos;
		Vec3 m_Orient;  // glm::quat
	}

	// A frame skeleton stores the joints of the skeleton for a single frame.
	public static class FrameSkeleton
	{
		List<SkeletonJoint> m_Joints;
	}

	int m_iMD5Version;
	int m_iNumFrames;
	int m_iNumJoints;
	int m_iFramRate;
	int m_iNumAnimatedComponents;

	float m_fAnimDuration;
	float m_fFrameDuration;
	float m_fAnimTime;

	private List<JointInfo>     m_JointInfos;
	private List<Bound>         m_Bounds;
	private List<BaseFrame>     m_BaseFrames;
	private List<FrameData>     m_Frames;
	private List<FrameSkeleton> m_Skeletons;    // All the skeletons for all the frames

	private FrameSkeleton m_AnimatedSkeleton;

	// Build the frame skeleton for a particular frame
	void BuildFrameSkeleton (
			List<FrameSkeleton> skeletons,
			List<JointInfo> jointInfo,
			List<BaseFrame> baseFrames,
			FrameData frameData)
	{

	}

	void InterpolateSkeletons (FrameSkeleton finalSkeleton, FrameSkeleton skeleton0, FrameSkeleton skeleton1, float fInterpolate)
	{

	}

	// Load an animation from the animation file
	boolean LoadAnimation (String filename)
	{
		return true;
	}

	// Update this animation's joint set.
	void Update (float fDeltaTime)
	{

	}

	// Draw the animated skeleton
	void Render ()
	{

	}

	FrameSkeleton GetSkeleton ()
	{
		return m_AnimatedSkeleton;
	}

	int GetNumJoints ()
	{
		return m_iNumJoints;
	}

	JointInfo GetJointInfo (int index)
	{
		assert (index < m_JointInfos.size());
		return m_JointInfos.get(index);
	}

}
