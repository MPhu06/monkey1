-- Database schema for Slide to Diagram application
-- PostgreSQL 16+

-- Create database (run as superuser)
-- CREATE DATABASE slidetodiagram;

-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create diagram_jobs table
CREATE TABLE IF NOT EXISTS diagram_jobs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    input_type VARCHAR(20) NOT NULL,
    input_text TEXT,
    diagram_type VARCHAR(30) NOT NULL,
    mermaid_code TEXT,
    image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_diagram_jobs_created_at ON diagram_jobs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_diagram_jobs_status ON diagram_jobs(status);

-- Create function to auto-update updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for auto-update
DROP TRIGGER IF EXISTS update_diagram_jobs_updated_at ON diagram_jobs;
CREATE TRIGGER update_diagram_jobs_updated_at
    BEFORE UPDATE ON diagram_jobs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE diagram_jobs IS 'Stores all diagram generation jobs';
COMMENT ON COLUMN diagram_jobs.input_type IS 'Input type: text or pptx';
COMMENT ON COLUMN diagram_jobs.diagram_type IS 'Diagram type: flowchart, sequence, mindmap, er, class, org';
COMMENT ON COLUMN diagram_jobs.status IS 'Job status: pending, completed, failed';
