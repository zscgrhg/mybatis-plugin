<script>
			<bind name="pattern" value="'%' + keyword + '%'" />
			select a.id, a.group_id, a.module_id, a.readable, a.writeable,
			 b.name, b.url, b.icon, b.fid, b.path
			from admin_group_privilege a, admin_module b 
			where a.module_id=b.id 
			 and a.group_id = (select group_id from admin_info where id=#{adminId,jdbcType=BIGINT}) 
			 and b.id not in (SELECT DISTINCT fid FROM admin_module)
			 and b.name like #{pattern}
			order by b.id asc;
			</script>