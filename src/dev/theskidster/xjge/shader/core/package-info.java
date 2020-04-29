/**
 * Encapsulates the OpenGL graphics pipeline into a single extensible static class which can be accessed through {@link ShaderCore}.
 * 
 * <p>
 * The shader-core class makes use of several {@link ShaderProgram} objects which in turn make use of several {@link ShaderSource} objects. These objects are 
 * initialized once during program startup through {@link dev.theskidster.xjge.main.App#glInit() App.glInit()}. During runtime, the shader-core can be used to 
 * provide supplementary data to the graphics pipeline through uniform variables, or to change the currently active shader program. Typically, the shader-core is 
 * accessed by objects implementing the {@link dev.theskidster.xjge.graphics.Graphics Graphics} component and calls made to it exhibit the following structure:
 * </p>
 * <br>
 * <blockquote><pre>
 * render() {
 *     <b>ShaderCore.use("some shader");</b>
 *     glBindVertexArray(g.vao);
 * 
 *     <b>haderCore.setInt("uType", 0);
 *     ShaderCore.setMat4("uModel", false, graphics.model);
 *     ShaderCore.setVec3("uColor", ambientColor);</b>
 *     ...
 * 
 *     glDrawElements();
 * }
 * </pre></blockquote>
 * @see dev.theskidster.xjge.graphics
 */
package dev.theskidster.xjge.shader.core;