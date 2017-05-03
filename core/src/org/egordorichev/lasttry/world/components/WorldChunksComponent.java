package org.egordorichev.lasttry.world.components;

import com.badlogic.gdx.Gdx;
import org.egordorichev.lasttry.Globals;
import org.egordorichev.lasttry.item.Item;
import org.egordorichev.lasttry.item.block.Block;
import org.egordorichev.lasttry.item.block.Wall;
import org.egordorichev.lasttry.util.Callable;
import org.egordorichev.lasttry.util.Camera;
import org.egordorichev.lasttry.util.Util;
import org.egordorichev.lasttry.world.World;
import org.egordorichev.lasttry.world.chunk.Chunk;
import org.egordorichev.lasttry.world.chunk.ChunkIO;

import java.util.ArrayList;

public class WorldChunksComponent extends WorldComponent {
	private Chunk[] chunks;
	private ArrayList<Chunk> loadedChunks = new ArrayList<>();
	private int size;
//	private static int debugTimerCounter = 20;

	public WorldChunksComponent(World world) {
		super(world);

		this.size = world.getWidth() * world.getHeight();
		this.chunks = new Chunk[this.size];

		Util.runInThread(new Callable() {
			@Override
			public void call() {
				updateLogic();
			}
		}, World.UPDATE_DELAY);
	}

	public void update() {

	}

	public void updateLogic() {
		for (int i = 0; i < this.loadedChunks.size(); i++) {
			this.loadedChunks.get(i).update();
		}
	}

	public void render() {
		int windowWidth = Gdx.graphics.getWidth();
		int windowHeight = Gdx.graphics.getHeight();
		int tww = windowWidth / Block.SIZE;
		int twh = windowHeight / Block.SIZE;
		int tcx = (int) (Camera.game.position.x - windowWidth / 2) / Block.SIZE;
		int tcy = (int) ((Camera.game.position.y - windowHeight / 2) / Block.SIZE);

		int minY = Math.max(0, tcy - 2);
		int maxY = Math.min(this.world.getHeight() - 1, tcy + twh + 3);
		int minX = Math.max(0, tcx - 2);
		int maxX = Math.min(this.world.getWidth() - 1, tcx + tww + 2);

		for (int y = minY; y < maxY; y++) {
			for (int x = minX; x < maxX; x++) {
				Wall wall = (Wall) Item.fromID(this.world.walls.getID(x, y));
				Block block = (Block) Item.fromID(this.world.blocks.getID(x, y));

				if (wall != null) {
					wall.renderWall(x, y);
				}

				if (block != null) {
					block.updateBlockStyle(x, y);
					block.renderBlock(x, y);
				}
			}
		}
	}

	public void load(int x, int y) {
		int index = this.getIndex(x, y);

		if (!this.isInside(index)) {
			return;
		}

		this.set(ChunkIO.load(x, y), x, y);
		this.loadedChunks.add(this.chunks[index]);
	}

	public void set(Chunk chunk, int x, int y) {
		int index = this.getIndex(x, y);

		if (!this.isInside(index)) {
			return;
		}

		this.chunks[index] = chunk;
	}

	public boolean isLoaded(int index) {
		if (!this.isInside(index)) {
			return false;
		}

		return this.chunks[index] != null;
	}

	public Chunk get(int x, int y) {
		int index = this.getIndex(x, y);

		if (!this.isInside(index)) {
			return null;
		}

		return this.chunks[index];
	}

	public Chunk getFor(int x, int y) {
		x /= Chunk.SIZE;
		y /= Chunk.SIZE;

		Chunk chunk = this.get(x, y);

		if (chunk == null) {
			this.load(x, y);
			return this.get(x, y);
		}

		return chunk;
	}

	private boolean isInside(int index) {
		if (index >= this.size || index < 0) {
			return false;
		}

		return true;
	}

	private int getIndex(int x, int y) {
		return x + y * this.world.getWidth() / Chunk.SIZE;
	}

	public void save() {
		for (int y = 0; y < Globals.world.getHeight() / Chunk.SIZE; y++) {
			for (int x = 0; x < Globals.world.getWidth() / Chunk.SIZE; x++) {
				if (this.get(x, y) != null) {
					ChunkIO.save(x, y);
				}
			}
		}
	}
}