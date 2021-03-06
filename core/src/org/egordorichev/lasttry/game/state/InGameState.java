package org.egordorichev.lasttry.game.state;

import org.egordorichev.lasttry.entity.Entity;
import org.egordorichev.lasttry.entity.engine.Engine;
import org.egordorichev.lasttry.entity.engine.system.systems.CameraSystem;
import org.egordorichev.lasttry.entity.entities.camera.CameraComponent;
import org.egordorichev.lasttry.graphics.Graphics;

public class InGameState extends State {
	public InGameState() {

	}

	/**
	 * Updates the game
	 *
	 * @param delta Time, since the last frame
	 */
	@Override
	public void update(float delta) {
		Engine.update(delta);
	}

	/**
	 * Renders the game
	 */
	@Override
	public void render() {
		for (Entity entity : Engine.getEntities()) {
			entity.render();
		}

		Graphics.batch.setProjectionMatrix(CameraSystem.instance.get("ui").getComponent(CameraComponent.class).camera.combined);

		for (Entity entity : Engine.getEntities()) {
			entity.renderUi();
		}
	}
}